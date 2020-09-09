package com.chat.server.server.impl;

import com.chat.server.config.AppConfiguration;
import com.chat.server.config.SessionSocketHolder;
import com.chat.server.handle.NioWebSocketChannelInitializer;
import com.chat.server.model.ChatMsg;
import com.chat.server.server.Server;
import com.chat.server.zk.RegistryZK;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.InetAddress;

@Log4j2
@Component
public class WebsocketServer implements Server {


    private EventLoopGroup boss = new NioEventLoopGroup();
    private EventLoopGroup work = new NioEventLoopGroup();
    private ChannelFuture future;
    @Autowired
    private AppConfiguration appConfiguration;

    /**
     * 启动server
     */
    @Override
    @PostConstruct
    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap().group(boss, work)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new NioWebSocketChannelInitializer());
        bind(bootstrap, appConfiguration.getChatServerPort());

    }

    private static void bind(final ServerBootstrap bootstrap, int port ) {
        final int p = port;
        bootstrap.bind(port).addListener(new GenericFutureListener<Future<? super Void>>() {
            @Override
            public void operationComplete(Future<? super Void> future) throws Exception {
                if (future.isSuccess()) {
                    System.out.println("端口[" + p + "]" + "绑定成功!");
                    String addr = InetAddress.getLocalHost().getHostAddress();
                    Thread s = new Thread(new RegistryZK(addr,p));
                    s.setName("registry-zk");
                    s.start();
                    log.info("websocket ip 端口 注册到zk");
                } else {
                    System.out.println("端口[" + p + "]" + "绑定失败!");
                    bind(bootstrap, p + 1);
                }
            }
        });
    }

    @PreDestroy
    public void stop() {
        boss.shutdownGracefully().syncUninterruptibly();
        work.shutdownGracefully().syncUninterruptibly();
        log.info("Close cim server success!!!");
    }

    public void sendMsg(ChatMsg sendMsgReqVO) {
        NioSocketChannel socketChannel = SessionSocketHolder.get(sendMsgReqVO.getUserId());

        if (null == socketChannel) {
            log.error("client {} offline!", sendMsgReqVO.getUserId());
        }
        ChannelFuture future = socketChannel.writeAndFlush(sendMsgReqVO);
        future.addListener((ChannelFutureListener) channelFuture ->
                log.info("server push msg:[{}]", sendMsgReqVO.toString()));
    }
}

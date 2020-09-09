package com.chat.server.handle;

import com.chat.server.client.WebSocketClient;
import com.chat.server.config.SessionSocketHolder;
import com.chat.server.model.ChatMsg;
import com.chat.server.model.FuncodeEnum;
import com.chat.server.util.RedisUtil;
import com.chat.server.util.SpringBeanFactory;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Objects;

import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;


@Slf4j
public class NioWebSocketHandler extends SimpleChannelInboundHandler<Object> {


    private WebSocketServerHandshaker handshaker;

    private AttributeKey<String> key = AttributeKey.valueOf("Id");


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("收到消息：" + msg);
        if (msg instanceof FullHttpRequest) {
            //以http请求形式接入，但是走的是websocket
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            //处理websocket客户端的消息
            handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //添加连接
        log.info("客户端加入连接：" + ctx.channel());
//        ChannelSupervise.addChannel(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //断开连接
        log.info("客户端断开连接：" + ctx.channel());
        removeUserInfo((NioSocketChannel) ctx.channel());
        Gson gson = new Gson();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    private void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 判断是否关闭链路的指令
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        // 判断是否ping消息
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(
                    new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        // 本例程仅支持文本消息，不支持二进制消息
        if (!(frame instanceof TextWebSocketFrame)) {
            log.info("本例程仅支持文本消息，不支持二进制消息");
            throw new UnsupportedOperationException(String.format(
                    "%s frame types not supported", frame.getClass().getName()));
        }
        // 返回应答消息
        String request = ((TextWebSocketFrame) frame).text();
        log.info("服务端收到：" + request);
        Gson gson = new Gson();
        ChatMsg chatMsg = gson.fromJson(request, ChatMsg.class);
        String userId=chatMsg.getUserId();
        if(StringUtils.isEmpty(userId)){
           //标识id不存在直接抛出
            return;
        }
        String frendId=chatMsg.getFriendId();
        if (FuncodeEnum.OPERATETYPE_LOGIN.getCode().equals(chatMsg.getType())) {
            saveUserInfo(chatMsg, (NioSocketChannel) ctx.channel());
            ctx.channel().writeAndFlush(new TextWebSocketFrame("登陆成功"));
            log.info("服务端收到：" + request);
            return;
        }
        //单聊消息
        if(FuncodeEnum.OPERATETYPE_SINGLECHAT.getCode().equals(chatMsg.getType())){
            if(StringUtils.isEmpty(frendId)){
                return;
            }
            NioSocketChannel friendchannel =  SessionSocketHolder.get(frendId);
            //判断当前用户有没有登录
            if(!Objects.isNull(friendchannel)){
                System.out.println("channel信息为:"+friendchannel);
                friendchannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(chatMsg)
                       ));
            }else{
                //获取redis的记录判断用户是否在线
                RedisUtil util = SpringBeanFactory.getBean(RedisUtil.class);
                String host = (String) util.get(chatMsg.getFriendId());
                if (!StringUtils.isEmpty(host)) {
                        WebSocketClient client = SessionSocketHolder.CLUSTER.get(host);
                        client.send(request);
                    log.info("服务端转发：" + request);
                    return;
                }
            }
        }


//        TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()
//                + ctx.channel().id() + "：" + request);


    }

    /**
     * 唯一的一次http请求，用于创建websocket
     */
    private void handleHttpRequest(ChannelHandlerContext ctx,
                                   FullHttpRequest req) {
        //要求Upgrade为websocket，过滤掉get/Post
        if (!req.decoderResult().isSuccess()
                || (!"websocket".equals(req.headers().get("Upgrade")))) {
            //若不是websocket方式，则创建BAD_REQUEST的req，返回给客户端
//            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(
//                    HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
            return;
        }
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(
                "ws://localhost:11211/websocket", null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory
                    .sendUnsupportedVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
        }
        log.info("消息来了");
    }

    /**
     * 拒绝不合法的请求，并返回错误信息
     */
    private static void sendHttpResponse(ChannelHandlerContext ctx,
                                         FullHttpRequest req, DefaultFullHttpResponse res) {
        // 返回应答给客户端
        if (res.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(),
                    CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
        }
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        // 如果是非Keep-Alive，关闭连接
        if (!isKeepAlive(req) || res.status().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void saveUserInfo(ChatMsg msg, NioSocketChannel channel) {
        //保存userid->保存channel
        if (Objects.isNull(SessionSocketHolder.get(msg.getUserId()))) {
            SessionSocketHolder.put(msg.getUserId(), channel);
        }
        //保存channel->userid
        if (!channel.hasAttr(key) || Objects.isNull(channel.attr(key))) {
            channel.attr(key).set(msg.getUserId());
        }
        RedisUtil redisUtil = SpringBeanFactory.getBean(RedisUtil.class);
        redisUtil.set(msg.getUserId(), SessionSocketHolder.getSelfHost());
        log.info("redis中的值为" + redisUtil.get(msg.getUserId()));
    }

    private void removeUserInfo(NioSocketChannel channel) {
        String userId = channel.attr(key).get();
        if (!StringUtils.isEmpty(userId)) {
            SessionSocketHolder.remove(userId);
            System.out.println("remove");
        } else {
            SessionSocketHolder.remove(channel);
        }
        SessionSocketHolder.CHANNEL_MAP.entrySet().stream().forEach(s -> {
            System.out.println("key:" + s.getKey() + "----value:" + s.getValue());
        });
    }
}
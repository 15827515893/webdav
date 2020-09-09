package com.chat.server.client;

import com.chat.server.config.SessionSocketHolder;
import com.chat.server.model.ChatMsg;
import com.chat.server.model.FuncodeEnum;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

@Slf4j
public class WebSocketClient extends org.java_websocket.client.WebSocketClient {

    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public WebSocketClient(String url) throws URISyntaxException {
        super(new URI(url));
    }


    @Override
    public void onOpen(ServerHandshake shake) {
//        log.info("握手..." );
//        for (Iterator<String> it = shake.iterateHttpFields(); it.hasNext(); ) {
//            String key = it.next();
//            System.out.println(key + ":" + shake.getFieldValue(key));
//        }
    }

    @Override
    public void onMessage(String paramString) {
        log.info("接收到消息：" + paramString);
    }

    @Override
    public void onClose(int paramInt, String paramString, boolean paramBoolean) {
        log.warn("关闭...");
        SessionSocketHolder.CLUSTER.remove(getHost());
    }

    @Override
    public void onError(Exception e) {
        log.error("异常" + e);

    }


    public static boolean isOpen(WebSocketClient client) {
        return client.getReadyState().equals(READYSTATE.OPEN);
    }

    public static void main(String[] args) {
        try {
            clien1();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void  clien1() throws Exception{
        WebSocketClient client = new WebSocketClient("ws://127.0.0.1:11211/websocket");
        client.connect();
        while (!client.getReadyState().equals(READYSTATE.OPEN)) {
            Thread.sleep(3000);
            System.out.println("还没有打开");
        }
        System.out.println("建立websocket连接");
        ChatMsg msg  = ChatMsg.builder().message("第一条消息").userId("one").type(FuncodeEnum.OPERATETYPE_LOGIN.getCode()).build();
        Gson gson = new Gson();
        client.send(gson.toJson(msg));
    }

}
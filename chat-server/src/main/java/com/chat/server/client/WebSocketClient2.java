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
public class WebSocketClient2 extends org.java_websocket.client.WebSocketClient {

    private String host;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public WebSocketClient2(String url) throws URISyntaxException {
        super(new URI(url));
    }


    @Override
    public void onOpen(ServerHandshake shake) {
        log.info("握手..." );
        for (Iterator<String> it = shake.iterateHttpFields(); it.hasNext(); ) {
            String key = it.next();
            System.out.println(key + ":" + shake.getFieldValue(key));
        }
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

//    public  WebSocketClient getClient(String ip, int port) throws Exception {
//        String host = "ws://" + ip + ":" + port + "/websocket";
//        client = new WebSocketClient(host);
//        client.connect();
//        while (!client.getReadyState().equals(READYSTATE.OPEN)) {
//            log.warn("还没有打开");
//            Thread.sleep(2000);
//        }
//        log.warn("建立websocket连接");
//        return client;
//    }

    public static boolean isOpen(WebSocketClient2 client) {
        return client.getReadyState().equals(READYSTATE.OPEN);
    }

    public static void main(String[] args) {
        try {
            clien2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void  clien2() throws Exception{
        WebSocketClient2 client = new WebSocketClient2("ws://127.0.0.1:11212/websocket");
        client.connect();
        while (!client.getReadyState().equals(READYSTATE.OPEN)) {
            System.out.println("还没有打开");
        }
        System.out.println("建立websocket连接");
        ChatMsg msg  = ChatMsg.builder().message("第一条消息").userId("two").type(FuncodeEnum.OPERATETYPE_LOGIN.getCode()).build();
        Gson gson = new Gson();
        client.send(gson.toJson(msg));
        while(true){
            ChatMsg msg2  = ChatMsg.builder().message("第一条消息").userId("two").friendId("one").type(FuncodeEnum.OPERATETYPE_SINGLECHAT.getCode()).build();
            Thread.sleep(5000);
            client.send(gson.toJson(msg2));
        }
    }

}
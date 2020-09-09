package com.chat.server.config;


import com.chat.server.client.WebSocketClient;
import com.chat.server.model.ChatMsg;
import com.chat.server.model.FuncodeEnum;
import com.google.gson.Gson;
import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.ZkClient;
import org.java_websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Configuration
public class BeanConfig  {
    @Autowired
    private AppConfiguration appConfiguration;

    @Bean
    public ZkClient buildZkClient() {
        ZkClient client =
                new ZkClient(appConfiguration.getZkAddr(), appConfiguration.getZkConnectTimeout());
        client.subscribeChildChanges(appConfiguration.getZkRoot(), new IZkChildListener() {
            @Override
            public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
                System.out.println(parentPath + "zk节点发生变化: " + currentChilds);
                modifyClusterConn(currentChilds);
            }
        });
        return client;
    }

    /**
     * 更新集群websocket链接信息,只做增量更新
     */
    private void modifyClusterConn(List<String> currentChilds) {
        String self = SessionSocketHolder.getSelfHost();
        Map<String, WebSocketClient> clusterMap = SessionSocketHolder.CLUSTER;
        currentChilds.forEach(host -> {
            if (host.equals(self)) {
                return;
            }
            if (Objects.isNull(clusterMap.get(host))) {
                int ipEnd = host.indexOf("-");
                int portStart = host.indexOf("-websocket:");
                String ip = host.substring(5, ipEnd);
                int port = Integer.valueOf(host.substring(portStart + 11, host.length()));
                try {
                    String url = "ws://" + ip + ":" + port + "/websocket";
                    System.out.println(url);
                    WebSocketClient client = new WebSocketClient(url);
                    client.connect();
                    while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
                        Thread.sleep(3000);
                        System.out.println("还没有打开");
                    }
                    client.setHost(host);
                    clusterMap.put(host,client);
                    ChatMsg msg = ChatMsg.builder().message("服务器登录消息").userId(self).type(FuncodeEnum.OPERATETYPE_LOGIN.getCode()).build();
                    Gson gson = new Gson();
                    client.send(gson.toJson(msg));
                } catch (Exception e) {
                    System.out.println(e);
                    System.out.println("发送失败");
                }
            }
        });
    }

}

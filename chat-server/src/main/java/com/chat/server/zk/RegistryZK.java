package com.chat.server.zk;

import com.chat.server.config.AppConfiguration;
import com.chat.server.config.SessionSocketHolder;
import com.chat.server.util.SpringBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

@Slf4j
public class RegistryZK implements Runnable {

    private ZkClient zkClient;

    private AppConfiguration appConfiguration;

    private String ip;

    private int websocketPort;


    public RegistryZK(String ip, int websocketPort) {
        this.appConfiguration = SpringBeanFactory.getBean(AppConfiguration.class);
        this.zkClient = SpringBeanFactory.getBean(ZkClient.class);
        this.ip = ip;
        this.websocketPort = websocketPort;
    }


    @Override
    public void run() {
        String childenPath = appConfiguration.getZkRoot() + "/host:" + ip + "-websocket:" + websocketPort;
        SessionSocketHolder.setSelfHost(childenPath.replace("/route/", ""));
        //创建父节点 route
        boolean exists = zkClient.exists(appConfiguration.getZkRoot());
        if (!exists) {
            zkClient.createPersistent(appConfiguration.getZkRoot());
        }
        zkClient.deleteRecursive(appConfiguration.getZkRoot() + "/host:" + ip + "-websocket:" + websocketPort);
        zkClient.createEphemeral(appConfiguration.getZkRoot() + "/host:" + ip + "-websocket:" + websocketPort);

    }
}

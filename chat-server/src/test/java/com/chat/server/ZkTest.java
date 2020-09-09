package com.chat.server;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;

@Slf4j
public class ZkTest {
    public static void main(String[] args) {
        ZkClient zkClient  =  new ZkClient("127.0.0.1:2181",10000);
//        zkClient.getChildren("/route").forEach(s->{
//            log.info(s);
//        });
        zkClient.createEphemeral("/route/sss");
    }
}

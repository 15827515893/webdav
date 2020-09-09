package com.chat.server.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AppConfiguration {

    @Value("${app.zk.root}")
    private String zkRoot;

    @Value("${app.zk.addr}")
    private String zkAddr;

    @Value("${app.zk.switch}")
    private boolean zkSwitch;

    @Value("${chat.server.port}")
    private int chatServerPort;

//    @Value("${cim.route.url}")
//    private String routeUrl ;

//    public String getRouteUrl() {
//        return routeUrl;
//    }
//
//    public void setRouteUrl(String routeUrl) {
//        this.routeUrl = routeUrl;
//    }

    @Value("${chat.server.heartbeat-time}")
    private long heartBeatTime ;

    @Value("${app.zk.connect-timeout}")
    private int zkConnectTimeout;
}


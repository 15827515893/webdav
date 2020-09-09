package com.chat.server;

public class IndexOftest {
    public static void main(String[] args) {
        String host = "host:172.22.144.1-websocket:11211";
        int ipEnd = host.indexOf("-");
        int portStart = host.indexOf("-websocket:");
        String ip = host.substring(5, ipEnd);
        int port = Integer.valueOf(host.substring(portStart + 11, host.length()));
        System.out.println(port);
    }
}

package com.chat.server.config;

import com.chat.server.client.WebSocketClient;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 22/05/2018 18:33
 * @since JDK 1.8
 */
public class SessionSocketHolder {

    private static String selfHost;

    public static String getSelfHost() {
        return selfHost;
    }

    public static void setSelfHost(String selfHost) {
        SessionSocketHolder.selfHost = selfHost;
    }

    public static final Map<String, NioSocketChannel> CHANNEL_MAP = new ConcurrentHashMap<>(16);
    //保存服务器客户端
    public static final Map<String, WebSocketClient> CLUSTER = new ConcurrentHashMap<>(16);



    /**
     * Save the relationship between the userId and the channel.
     *
     * @param id
     * @param socketChannel
     */
    public static void put(String id, NioSocketChannel socketChannel) {
        CHANNEL_MAP.put(id, socketChannel);
    }

    public static NioSocketChannel get(String id) {
        return CHANNEL_MAP.get(id);
    }

    public static Map<String, NioSocketChannel> getRelationShip() {
        return CHANNEL_MAP;
    }

    public static void remove(NioSocketChannel nioSocketChannel) {
        CHANNEL_MAP.entrySet().stream().filter(entry -> entry.getValue() == nioSocketChannel).forEach(entry -> CHANNEL_MAP.remove(entry.getKey()));
    }

    public static void remove(String id) {
        CHANNEL_MAP.remove(id);
    }

}

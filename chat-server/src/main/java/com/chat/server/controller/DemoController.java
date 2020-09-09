package com.chat.server.controller;

import com.chat.server.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("test")
public class DemoController {
    @Autowired
    private RedisUtil redisUtil;


    @GetMapping
    public Map testGet() {
        return new HashMap<String, String>(){{
            put("name", "springboot");
        }};
    }

    @GetMapping(path = "str")
    public String testGetStr() {
        redisUtil.set("aa","bb");
        return redisUtil.get("aa").toString();
    }

    @GetMapping(path = "all")
    public String redisAll() {
        redisUtil.set("aa","bb");
        return redisUtil.get("aa").toString();
    }
}

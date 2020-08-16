package com.pj.webmagic.commons;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class Producer {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    public void sendMessage(String message) {
        redisTemplate.convertAndSend("channel", message);
    }
}

package com.king.ruby.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;

public class RedisXia {
    public static void main(String[] args) {
//        Jedis jedis = new Jedis("127.0.0.1",6379);
        Jedis jedis = new Jedis("47.112.176.227",6379);
        System.out.println(jedis.keys("*"));
    }

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Test
    void contextLoads() {
        redisTemplate.opsForValue().set("myKey","诸葛孔暗学架构");
        System.out.println(redisTemplate.opsForValue().get("myKey"));
    }

}

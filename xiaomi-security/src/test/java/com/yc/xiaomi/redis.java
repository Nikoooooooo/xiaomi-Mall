package com.yc.xiaomi;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

@SpringBootTest
public class redis {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testString(){
        System.out.println(redisTemplate);
        //写入一条String数据
        redisTemplate.opsForValue().set("name","小红");
        //获取String数据
        Object name=redisTemplate.opsForValue().get("name");
        System.out.println(name);
    }
}

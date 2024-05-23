package com.redis;


import com.redis.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.stereotype.Component;


@SpringBootTest
public class RedisDemoApplicationTest {
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;



    @Test
    public void testString(){
        System.out.println(redisTemplate);
        //写入一条String数据
         redisTemplate.opsForValue().set("name","小红");
        //获取String数据
        Object name=redisTemplate.opsForValue().get("name");
        System.out.println(name);
    }

    @Test
    void testSaveUser(){
        //写入数据
//        redisTemplate.opsForValue().set("user:100",new User("小鸟伏特加",998));
//        //获取数据
//        User user= (User) redisTemplate.opsForValue().get("user:100");
//        System.out.println("用户为:"+user);
    }



    }

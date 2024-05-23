package com.redis;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.entity.User;
import net.minidev.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.*;


@SpringBootTest
public class RedisStringTest {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    @Test
    public void testString(){
        //写入一条String数据
        stringRedisTemplate.opsForValue().setIfAbsent("voucherUid:3","3");
        stringRedisTemplate.opsForValue().setIfAbsent("voucherUid:4","4");

        Set<String> result=stringRedisTemplate.keys("voucherUid:*");
        List<String> values=stringRedisTemplate.opsForValue().multiGet(result);
        System.out.println(values);
        //stringRedisTemplate.opsForList().rightPush("voucherUid","92");
        //stringRedisTemplate.opsForList().rightPush("num","2");
        //stringRedisTemplate.opsForList().rightPush("num","3");
        //获取String数据
        //Object name=stringRedisTemplate.opsForValue().get("num");
        //System.out.println(name);
    }

    private static final ObjectMapper mapper=new ObjectMapper();

    @Test
    void testSaveUser() throws JsonProcessingException {
//        //创建对象
//        User user=new User(13,"李四","18860054522","aaa","2024.3.11","2711837873@qq.com");
//        String json=mapper.writeValueAsString(user);
//        stringRedisTemplate.opsForList().leftPush("user:92",json);
//        //获取数据
//       List<String> list = stringRedisTemplate.opsForList().range("user:92",0,3);
//       List<Map<String,Object>> newList=new ArrayList<>();
//        for (int i = 0; i < list.size(); i++) {
//            newList.add(mapper.readValue(list.get(i),Map.class));
//        }
//        System.out.println(newList);
        List<Integer> list=new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(i);
        }
        list.remove(0);
        list.remove(1);
        list.remove(2);
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
        }

    }

    @Test
    void testHash(){
        stringRedisTemplate.opsForHash().put("user:400","name","狗蛋");
        stringRedisTemplate.opsForHash().put("user:400","age","8");

        Map<Object,Object> entries=stringRedisTemplate.opsForHash().entries("user:400");
        System.out.println("entries ="+entries);
    }


    }

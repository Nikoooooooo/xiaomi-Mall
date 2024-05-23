package com.yc.xiaomi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RSet;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
/*
* 测试优惠券秒杀功能
* */
public class testVoucherKill {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;



    @Test
    public void testLock(){
//        RAtomicLong stock=redissonClient.getAtomicLong("num");
//        RSet<String> userSet=redissonClient.getSet("voucherUid");
//        Long result1=stock.get();
//        System.out.println(result1+"----------");
//        boolean result2=userSet.contains("92");
//        System.out.println(result2+"----------");
        String value= stringRedisTemplate.opsForValue().get("voucher");
        System.out.println(value);
    }
}

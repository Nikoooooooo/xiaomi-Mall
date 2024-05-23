package com.yc.xiaomi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;
/*
* 测试分布式锁
* */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRedissonLock {


    @Autowired
    private RedissonClient redissonClient;

    @Test
    public void testRedisson() throws InterruptedException {
        //获取锁(可重入),指定锁的名称
        RLock lock=redissonClient.getLock("testLock");//锁的key
        //尝试获取锁
        //参数分别是:获取锁的最大等待时间(期间会重试),锁自动释放时间,时间单位
        boolean isLock=lock.tryLock(1,10, TimeUnit.SECONDS);
        //判断是否获取锁成功
        if (isLock){
            try {
                System.out.println("执行成功");
            }finally {
                //释放锁
                lock.unlock();
                //TODO:写入日志中
            }
        }
    }

}

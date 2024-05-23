package com.yc.xiaomi;

import com.yc.xiaomi.dao.UserDao;
import com.yc.xiaomi.model.BloomFilterConstant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBloomFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BloomTest {
    //注入指定的布隆过滤器
    @Resource
    @Qualifier(BloomFilterConstant.NAME_SECKILL)
    private RBloomFilter<Object> bloomFilter;

    @Autowired
    private UserDao userdao;


    //测试添加功能
    @Test
    public void testBloomAdd(){
        List<String> list=  userdao.selectAllId();


        for (String data: list){
           bloomFilter.add(data);
        }

        System.out.println( bloomFilter.contains("92"));
    }

}

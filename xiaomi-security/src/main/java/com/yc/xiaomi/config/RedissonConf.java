package com.yc.xiaomi.config;

import com.yc.xiaomi.model.BloomFilterConstant;
import io.swagger.models.auth.In;
import org.redisson.Redisson;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
* Redisson配置类
* */

@EnableCaching
@Configuration
public class RedissonConf {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.database}")
    private Integer database;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(){
        /**
         *  连接哨兵：config.useSentinelServers().setMasterName("myMaster").addSentinelAddress()
         *  连接集群：config.useClusterServers().addNodeAddress()
         *  连接主从：config.useMasterSlaveServers().setMasterAddress("xxx").addSlaveAddress("xxx")
         */
        // 连接单机
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://"+host+":"+port)
                .setPassword(password)
                .setDatabase(database);
        RedissonClient client = Redisson.create(config);
        return client;
    }

    @Bean(BloomFilterConstant.NAME_SECKILL)
    public RBloomFilter<Object> seckillBloomFilter(){
        RBloomFilter<Object> bloomFilter = redissonClient().getBloomFilter(BloomFilterConstant.NAME_SECKILL);
        bloomFilter.tryInit(BloomFilterConstant.EXPECTED_INSERTIONS_SECKILL, BloomFilterConstant.FALSE_POSITIVERATE_SECKILL);
        return bloomFilter;
    }



}

package com.yc.xiaomi.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

//redis缓存类
@Component
@Slf4j
public class CacheService {
    @Autowired
    private StringRedisTemplate redisTemplate;

    private ObjectMapper mapper;

    private final String DEFAULT_KEY_PREFIX = "";   //key字符串非空处理
    private final int EXPIRE_TIME = 1;
    private final TimeUnit EXPIRE_TIME_TYPE = TimeUnit.DAYS;

    /**
     * 数据缓存至redis
     *
     * @param key
     * @param value
     * @return
     */
    public <K, V> void add(K key, V value) {
        try {
            if (value != null) {
                redisTemplate
                        .opsForList()
                        .rightPush(DEFAULT_KEY_PREFIX + key, mapper.writeValueAsString(value));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("数据缓存至redis失败");
        }
    }

    /**
     * 数据缓存至redis并设置过期时间
     *
     * @param key
     * @param value
     * @return
     */
    public <K, V> void add(K key, V value, long timeout, TimeUnit unit) {
        try {
            if (value != null) {
                redisTemplate
                        .opsForList()
                        .rightPush(DEFAULT_KEY_PREFIX + key, mapper.writeValueAsString(value));
                redisTemplate
                        .expire(DEFAULT_KEY_PREFIX + key, timeout, unit);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException("数据缓存至redis失败");
        }
    }


}



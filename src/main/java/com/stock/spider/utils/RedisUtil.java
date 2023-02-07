package com.stock.spider.utils;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

@Component
public class RedisUtil {

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    public Set<String> getKeysByString(String val) {
        Set<String> keys = redisTemplate.keys(val);
        return keys;
    }

    public String getValueByString(String key) {
        String value = redisTemplate.opsForValue().get(key).toString();
        return value;
    }

    public void setByString(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

}

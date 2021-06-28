package com.stock.spider.utils;

import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RedisUtil {

    @Resource
    RedisTemplate<String, Object> redisTemplate;

    //string
    public Set<String> getKeysByString(String key) {
        Set<String> keys = redisTemplate.keys(key);
        return keys;
    }

    //string
    public String getValueByString(String key) {
        String value = redisTemplate.opsForValue().get(key).toString();
        return value;
    }

    //string
    public void setByString(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    //hashmap
    public Map<String, String> getByHash(String key) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
        Map<String, String> collect = entries.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
        return collect;
    }

    //hashmap
    public void setByHash(String key, Map<String, String> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    //切换数据库
    public void selectDataBase(int db) {
        LettuceConnectionFactory connectionFactory = (LettuceConnectionFactory) redisTemplate.getConnectionFactory();
        connectionFactory.setDatabase(db);
        redisTemplate.setConnectionFactory(connectionFactory);
        connectionFactory.afterPropertiesSet();
        connectionFactory.resetConnection();
    }
}

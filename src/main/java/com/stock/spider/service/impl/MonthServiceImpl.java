package com.stock.spider.service.impl;

import com.stock.spider.service.MonthService;
import com.stock.spider.utils.RedisUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class MonthServiceImpl implements MonthService {
    @Resource
    RedisUtil redisUtil;

    @Override
    public List<Map<String, String>> month(String current) {
        List<Map<String, String>> result = new ArrayList<>();

        Map<String, String> industrySortedMap_current = this.getMonth(current);

        result.add(industrySortedMap_current);

        return result;
    }

    private Map<String, String> getMonth(String month) {
        redisUtil.selectDataBase(0);
        Map<String, String> industryHashMap = new HashMap();
        Set<String> keys = redisUtil.getKeysByString("*");
        redisUtil.selectDataBase(1);
        keys.stream().forEach(e -> industryHashMap.put(e,
                String.valueOf(redisUtil.getByHash(e).entrySet().stream()
                        .filter(i -> i.getKey().split("-")[1].equals(month))//每年的月份
                        .filter(i -> Double.parseDouble(i.getValue()) > 0)//涨
                        .count())));//涨的次数

        Map<String, String> industrySortedMap = new LinkedHashMap<>();
        industryHashMap.entrySet().stream()
                .sorted(Map.Entry.<String, String>comparingByValue().reversed())
                .filter(e -> Integer.valueOf(e.getValue()) >= 4)//2016-2020共5年
                .forEach(e -> industrySortedMap.put(e.getKey(), e.getValue()));

        return industrySortedMap;
    }
}


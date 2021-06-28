package com.stock.spider.service.impl;

import com.stock.spider.service.MonthService;
import com.stock.spider.utils.RedisUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MonthServiceImpl implements MonthService {
    @Resource
    RedisUtil redisUtil;

    @Override
    public List<Map<String, String>> month() {
        List<Map<String, String>> result = new ArrayList<>();

        Map<String, String> industrySortedMap_6 = this.month("-6");

        Map<String, String> industrySortedMap_7 = this.month("-7");

        //求交集
        redisUtil.selectDataBase(0);
        Map<String, String> intersection = industrySortedMap_6.entrySet().stream()
                .filter(e -> industrySortedMap_7.entrySet().stream().map(Map.Entry::getKey)
                        .collect(Collectors.toList()).contains(e.getKey()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> redisUtil.getValueByString(e.getKey())));

        result.add(industrySortedMap_6);
        result.add(industrySortedMap_7);
        result.add(intersection);

        return result;
    }

    private Map<String, String> month(String month) {
        redisUtil.selectDataBase(0);
        Map<String, String> industryHashMap = new HashMap();
        Set<String> keys = redisUtil.getKeysByString("*");
        redisUtil.selectDataBase(1);
        keys.stream().filter(e -> !e.equals("BK0910")).forEach(e -> industryHashMap.put(e,
                String.valueOf(redisUtil.getByHash(e).entrySet().stream()
                        .filter(i -> i.getKey().contains(month))//每年的月份
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


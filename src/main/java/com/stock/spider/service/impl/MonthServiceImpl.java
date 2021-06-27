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
    public List<Map<String, Integer>> month() {
        List<Map<String, Integer>> result = new ArrayList<>();

        redisUtil.selectDataBase(0);
        Map<String, Integer> industryHashMap_6 = new HashMap();
        Set<String> keys_6 = redisUtil.getByString("*");
        redisUtil.selectDataBase(1);
        keys_6.stream().filter(e -> !e.equals("BK0910")).forEach(e -> industryHashMap_6.put(e,
                (int) redisUtil.getByHash(e).entrySet().stream()
                        .filter(i -> i.getKey().contains("-6"))//每年的6月份
                        .filter(i -> Double.parseDouble(i.getValue()) > 0)//涨
                        .count()));//涨的次数

        Map<String, Integer> industrySortedMap_6 = new LinkedHashMap<>();
        industryHashMap_6.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .filter(e -> e.getValue() >= 4)//2016-2020共5年
                .forEach(e -> industrySortedMap_6.put(e.getKey(), e.getValue()));

        redisUtil.selectDataBase(0);
        Map<String, Integer> industryHashMap_7 = new HashMap();
        Set<String> keys_7 = redisUtil.getByString("*");
        redisUtil.selectDataBase(1);
        keys_7.stream().filter(e -> !e.equals("BK0910")).forEach(e -> industryHashMap_7.put(e,
                (int) redisUtil.getByHash(e).entrySet().stream()
                        .filter(i -> i.getKey().contains("-7"))//每年的7月份
                        .filter(i -> Double.parseDouble(i.getValue()) > 0)//涨
                        .count()));//涨的次数

        Map<String, Integer> industrySortedMap_7 = new LinkedHashMap<>();
        industryHashMap_7.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .filter(e -> e.getValue() >= 4)//2016-2020共5年
                .forEach(e -> industrySortedMap_7.put(e.getKey(), e.getValue()));

        //求交集
        List<String> collect = industrySortedMap_7.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
        Map<String, Integer> industrySortedMap_6_7 = industrySortedMap_6.entrySet().stream()
                .filter(e -> collect.contains(e.getKey()))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

//        System.out.println("---6月份---分割线---");
//        industrySortedMap_6.entrySet().stream().forEach(e -> System.out.println(e.getKey() + "---" + e.getValue()));
//        System.out.println("---7月份---分割线---");
//        industrySortedMap_7.entrySet().stream().forEach(e -> System.out.println(e.getKey() + "---" + e.getValue()));
//        System.out.println("---6,7月份---分割线---");
//        industrySortedMap_6_7.entrySet().stream().forEach(e -> System.out.println(e.getKey() + "---" + e.getValue()));
        result.add(industrySortedMap_6);
        result.add(industrySortedMap_7);
        result.add(industrySortedMap_6_7);
        return result;
    }
}


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
        Map<String, Long> industryHashMap = new HashMap();
        Set<String> keys = redisUtil.getKeysByString("*");
        redisUtil.selectDataBase(1);
        keys.stream().forEach(e -> industryHashMap.put(e,
                redisUtil.getByHash(e).entrySet().stream()
                        .filter(i -> i.getKey().split("-")[1].equals(month))//每年的月份 月份没有前导0
                        .filter(i -> Double.parseDouble(i.getValue()) > 0)//涨
                        .count()));//涨的次数 从2016-2开始算起 截至2021-1最多4次,2021-2最多5次

        Map<String, String> industrySortedMap = new LinkedHashMap<>();
        Long maxValue = industryHashMap.entrySet().stream().max(Map.Entry.comparingByValue()).get().getValue();
        industryHashMap.entrySet().stream()
                .filter(e -> e.getValue().equals(maxValue))
                .forEach(e -> industrySortedMap.put(e.getKey(), String.valueOf(e.getValue())));

        return industrySortedMap;
    }
}


package com.stock.spider.service.impl;

import com.stock.spider.service.MonthService;
import com.stock.spider.service.StockCollectionsService;
import com.stock.spider.service.StockService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockCollectionsServiceImpl implements StockCollectionsService {

    @Resource
    MonthService monthService;

    @Resource
    StockService stockService;

    @Override
    public Map<BigDecimal, String> stockCollections(String type) {
        int curr = LocalDate.now().getMonthValue();
        int next = LocalDate.now().plusMonths(1).getMonthValue();
        int pre = LocalDate.now().minusMonths(1).getMonthValue();

        List<Map<String, String>> currentMonthList = monthService.month(String.valueOf(curr));
        Set<Map<String, String>> currentMonthSet = currentMonthList.stream().collect(Collectors.toSet());

        List<Map<String, String>> nextMonthList = monthService.month(String.valueOf(next));
        Set<Map<String, String>> nextMonthSet = nextMonthList.stream().collect(Collectors.toSet());

        List<Map<String, String>> preMonthList = monthService.month(String.valueOf(pre));
        Set<Map<String, String>> preMonthSet = preMonthList.stream().collect(Collectors.toSet());

        Set<Map<String, String>> allSet = new HashSet<>();//去重
        allSet.addAll(currentMonthSet);
        allSet.addAll(nextMonthSet);
        allSet.addAll(preMonthSet);

        List<String> allList = new ArrayList<>();
        allSet.stream().forEach(e -> allList.addAll(e.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList())));

        Map<BigDecimal, String> result;

        long start = System.currentTimeMillis();
        if ("top10".equals(type)) {
            result = this.top10Map(allList);
        } else if ("billion".equals(type)) {
            result = this.billionMap(allList);
        } else {
            result = this.billionAndTop10Map(this.billionMap(allList), this.top10Map(allList));
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时: " + (end - start) / 1000 + "秒");

        return result;
    }

    //成交额不小于十亿的
    private Map<BigDecimal, String> billionMap(List<String> allIndustryCodeList) {
        Map<BigDecimal, String> resultMap = new TreeMap<>();
        BigDecimal billion = new BigDecimal(10);
        allIndustryCodeList.stream().forEach(e -> {
            Map<BigDecimal, String> stockMap = stockService.stock(e);
            stockMap.entrySet().removeIf(i -> i.getKey().compareTo(billion) == -1);//去掉小于10亿的
            resultMap.putAll(stockMap);
        });
        return resultMap;
    }

    //各行业中取前10%
    private Map<BigDecimal, String> top10Map(List<String> allIndustryCodeList) {
        Map<BigDecimal, String> resultMap = new TreeMap<>();
        allIndustryCodeList.stream().forEach(e -> {
            Map<BigDecimal, String> stockMap = stockService.stock(e);
            Map<BigDecimal, String> collect = stockMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.reverseOrder()))
                    .limit(stockMap.size() / 10 + 1)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            resultMap.putAll(collect);
        });
        return resultMap;
    }

    //合并上述两种情况
    private Map<BigDecimal, String> billionAndTop10Map(Map<BigDecimal, String> billionMap,
                                                       Map<BigDecimal, String> top10Map) {
        Map<BigDecimal, String> allMap = new TreeMap<>();
        allMap.putAll(billionMap);
        allMap.putAll(top10Map);
        return allMap;
    }
}

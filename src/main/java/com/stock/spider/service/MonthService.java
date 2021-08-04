package com.stock.spider.service;

import java.util.List;
import java.util.Map;

/**
 * 统计每年的某一月份的涨跌情况
 * 一般情况下，10/15号为发工资的日子，所以假设每月的月中来进行买卖股票
 */
public interface MonthService {
    List<Map<String, String>> month(String current, String next);
}

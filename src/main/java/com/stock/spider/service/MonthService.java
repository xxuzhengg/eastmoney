package com.stock.spider.service;

import java.util.List;
import java.util.Map;

/**
 * 统计每年的某一月份的涨跌情况
 */
public interface MonthService {
    List<Map<String, String>> month(String current);
}

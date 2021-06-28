package com.stock.spider.service;

import java.util.List;
import java.util.Map;

/**
 * 统计每年的某一月份的涨跌情况
 * 一般情况下，10号为发工资的日子，所以假设每月的月中来进行买卖股票
 * 举个例子，比如当前为6月中，那么就统计每年的6月和7月的涨跌情况
 */
public interface MonthService {
    List<Map<String, String>> month();
}

package com.stock.spider.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 回顾过去月份的上涨概率以及上涨幅度
 */
public interface ReviewService {
    Map<BigDecimal, String> review(String date, String industryCode);
}

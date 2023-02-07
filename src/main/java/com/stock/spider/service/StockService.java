package com.stock.spider.service;

import java.math.BigDecimal;
import java.util.Map;

public interface StockService {
    Map<BigDecimal, String> stock(String industryCode);
}

package com.stock.spider.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * pre cur next 三个月份中的合集
 */
public interface StockCollectionsService {
    Map<BigDecimal, String> stockCollections(String type);
}

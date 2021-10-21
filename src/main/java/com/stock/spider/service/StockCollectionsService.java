package com.stock.spider.service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * pre cur next三个月份中的成交量大于10亿的合集
 */
public interface StockCollectionsService {
    Map<BigDecimal, String> stockCollections();
}

package com.stock.spider.service;

import java.math.BigDecimal;
import java.util.Map;

public interface IndustryService {
    void industry();

    Map<BigDecimal, String> industryKLine();
}
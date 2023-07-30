package com.stock.spider.service;

import com.stock.spider.entity.Data;

import java.util.List;

public interface StockService {
    List<Data> stock(String industryCode, String klt);
}

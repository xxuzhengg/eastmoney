package com.stock.spider.service;

import com.stock.spider.entity.Data;

import java.util.List;

public interface IndustryService {
    void industry();

    List<Data> industryKLine();
}
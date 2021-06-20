package com.stock.spider;

import com.stock.spider.service.IndustryKLineService;
import com.stock.spider.service.IndustryService;
import com.stock.spider.service.MonthService;
import com.stock.spider.service.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class SpiderApplicationTests {

    @Resource
    IndustryService industryService;
    @Resource
    IndustryKLineService industryKLineService;
    @Resource
    MonthService monthService;
    @Resource
    StockService stockService;

    @Test
    void industry() {
        industryService.industry();
    }

    @Test
    void industryKLine() {
        industryKLineService.industryKLine();
    }

    @Test
    void month() {
        monthService.month();
    }

    @Test
    void stock() {
        stockService.stock();
    }

}

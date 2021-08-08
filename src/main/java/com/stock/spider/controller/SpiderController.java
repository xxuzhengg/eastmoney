package com.stock.spider.controller;

import com.stock.spider.service.IndustryKLineService;
import com.stock.spider.service.IndustryService;
import com.stock.spider.service.MonthService;
import com.stock.spider.service.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/spider")
public class SpiderController {

    @Resource
    IndustryService industryService;

    @Resource
    IndustryKLineService industryKLineService;

    @Resource
    MonthService monthService;

    @Resource
    StockService stockService;

    @Resource
    ReviewService reviewService;

    @RequestMapping("/index")
    public String index() {
        return "spider";
    }

    @RequestMapping("/industry")
    @ResponseBody
    public String industry() {
        try {
            industryService.industry();
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @RequestMapping("/industryKLine")
    @ResponseBody
    public String industryKLine() {
        try {
            industryKLineService.industryKLine();
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @RequestMapping("/month/{current}/{next}")
    @ResponseBody
    public List<Map<String, String>> month(@PathVariable("current") String current, @PathVariable("next") String next) {
        List<Map<String, String>> month = monthService.month(current, next);
        return month;
    }

    @RequestMapping("/stock/{code}")
    @ResponseBody
    public Map<BigDecimal, String> stock(@PathVariable("code") String code) {
        Map<BigDecimal, String> stock = stockService.stock(code);
        return stock;
    }
}

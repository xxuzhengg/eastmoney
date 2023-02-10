package com.stock.spider.controller;

import com.stock.spider.entity.Data;
import com.stock.spider.entity.Result;
import com.stock.spider.service.IndustryService;
import com.stock.spider.service.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/spider")
public class SpiderController {

    @Resource
    IndustryService industryService;

    @Resource
    StockService stockService;

    @RequestMapping("/index")
    public String index() {
        return "spider";
    }

    @RequestMapping("/industry")
    @ResponseBody
    public Map<BigDecimal, String> industry() {
        industryService.industry();
        Map<BigDecimal, String> industryKLine = industryService.industryKLine();
        return industryKLine;
    }

    @RequestMapping("/stock/{code}")
    @ResponseBody
    public Result stock(@PathVariable("code") String code) {
        List<Data> dataList = stockService.stock(code);
        Result result = new Result();
        result.setCode("0");
        result.setMsg("success");
        result.setCount(dataList.size());
        result.setData(dataList);
        return result;
    }
}

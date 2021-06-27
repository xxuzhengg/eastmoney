package com.stock.spider.controller;

import com.stock.spider.service.IndustryKLineService;
import com.stock.spider.service.IndustryService;
import com.stock.spider.service.MonthService;
import com.stock.spider.service.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    @RequestMapping("/month")
    public String month(Model model) {
        List<Map<String, Integer>> month = monthService.month();
        model.addAttribute("month_6", month.get(0));
        model.addAttribute("month_7", month.get(1));
        model.addAttribute("month_6_7", month.get(2));
        return "spider";
    }

    @RequestMapping("/stock/{code}")
    public String stock(Model model, @PathVariable("code") String code) {
        Map<Double, String> stock = stockService.stock(code);
        model.addAttribute("stock", stock);
        return "spider";
    }
}

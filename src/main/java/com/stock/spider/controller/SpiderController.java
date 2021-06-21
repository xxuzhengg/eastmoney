package com.stock.spider.controller;

import com.stock.spider.service.IndustryKLineService;
import com.stock.spider.service.IndustryService;
import com.stock.spider.service.MonthService;
import com.stock.spider.service.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

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

    @RequestMapping("/industry")
    public String industry(Model model) {
        industryService.industry();
        model.addAttribute("message", "success");
        return "industry";
    }

    @RequestMapping("/industryKLine")
    public String industryKLine(Model model) {
        industryKLineService.industryKLine();
        model.addAttribute("message", "success");
        return "industryKLine";
    }

    @RequestMapping("/month")
    public String month(Model model) {
        monthService.month();
        model.addAttribute("message", "success");
        return "month";
    }

    @RequestMapping("/stock/{industryCode}")
    public String stock(Model model, @PathVariable("industryCode") String industryCode) {
        stockService.stock(industryCode);
        model.addAttribute("message", "success");
        return "stock";
    }
}

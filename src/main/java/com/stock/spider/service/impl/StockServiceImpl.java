package com.stock.spider.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.spider.entity.Data;
import com.stock.spider.service.StockService;
import com.stock.spider.utils.WebUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

@Service
public class StockServiceImpl implements StockService {
    @Resource
    TaskExecutor taskExecutor;

    @Resource
    WebUtil webUtil;

    @Resource
    ObjectMapper objectMapper;

    /**
     * stockApi
     */
    @Value("${stock.np}")
    private String np;
    @Value("${stock.pn}")
    private String pn;
    @Value("${stock.pz}")
    private String pz;
    @Value("${stock.fields}")
    private String fields;

    /**
     * stockKLineApi
     */
    @Value("${stockKLine.fields1}")
    private String fields1;
    @Value("${stockKLine.fields2}")
    private String fields2;
    @Value("${stockKLine.fqt}")
    private String fqt;
    @Value("${stockKLine.end}")
    private String end;
    @Value("${stockKLine.lmt}")
    private String lmt;

    /**
     * k线类型 (101日线 102周线 103月线 104季线 105半年线 106年线)
     */
    private final Map<String, Integer> kLineTypeMap = Map.of("101", 1, "102", 5, "103", 20, "104", 60, "105", 180, "106", 360);

    @Override
    public List<Data> stock(String industryCode, String klt) {
        String stockApi = "https://push2.eastmoney.com/api/qt/clist/get?np=%s&pn=%s&pz=%s&fs=b:%s&fields=%s";
        String stockKLineApi = "https://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=%s&fields2=%s&klt=%s&fqt=%s&secid=%s.%s&end=%s&lmt=%s";
        String industryKLineApi = "https://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=%s&fields2=%s&klt=%s&fqt=%s&secid=90.%s&end=%s&lmt=%s";

        List<Data> dataList = new CopyOnWriteArrayList<>();

        String formatStockApi = String.format(stockApi, np, pn, pz, industryCode, fields);
        String web = webUtil.getWeb(formatStockApi);
        JsonNode node = null;
        try {
            node = objectMapper.readTree(web).get("data").get("diff");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        CountDownLatch countDownLatch = new CountDownLatch(node.size());

        for (JsonNode stock : node) {
            taskExecutor.execute(() -> {
                try {
                    String code = stock.get("f12").asText();
                    String name = stock.get("f14").asText();
                    String type = "1";//沪股
                    if (code.startsWith("0") || code.startsWith("3")) {
                        type = "0";//深股
                    }
                    String formatStockKLineApi = String.format(stockKLineApi, fields1, fields2, klt, fqt, type, code, end, lmt);
                    String stockWeb = webUtil.getWeb(formatStockKLineApi);
                    String str = objectMapper.readTree(stockWeb).get("data").get("klines").get(0).asText();
                    BigDecimal tradingVolume = new BigDecimal(str.split(",")[0]).divide(new BigDecimal(1_0000));//以万为单位
                    BigDecimal tradingAmount = new BigDecimal(str.split(",")[1]).divide(new BigDecimal(1_0000_0000));//以亿为单位
                    BigDecimal stockIncrease = new BigDecimal(str.split(",")[2]);
                    String formatIndustryKLineApi = String.format(industryKLineApi, fields1, "f59", klt, fqt, industryCode, end, lmt);
                    String industryWeb = webUtil.getWeb(formatIndustryKLineApi);
                    BigDecimal industryIncrease = new BigDecimal(objectMapper.readTree(industryWeb).get("data").get("klines").get(0).asText());
                    Data data = new Data();
                    data.setStockCode(code);
                    data.setStockName(name);
                    data.setTradingVolumeAvg(tradingVolume.divide(new BigDecimal(kLineTypeMap.get(klt)), 2, RoundingMode.HALF_UP));
                    data.setTradingAmountAvg(tradingAmount.divide(new BigDecimal(kLineTypeMap.get(klt)), 2, RoundingMode.HALF_UP));
                    data.setScore(stockIncrease.subtract(industryIncrease));
                    data.setLine("<a href='https://quote.eastmoney.com/concept/" + (code.startsWith("60") ? "sh" : "sz") + code + ".html' target='_blank' style='color: red'>查看</a>");
                    data.setProfit("<a href='https://www.iwencai.com/unifiedwap/result?w=" + code + "收盘获利' target='_blank' style='color: blue'>查看</a>");
                    dataList.add(data);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return dataList;
    }
}


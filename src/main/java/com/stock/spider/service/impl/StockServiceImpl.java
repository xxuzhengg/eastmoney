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
import java.util.ArrayList;
import java.util.List;
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
    @Value("${stockKLine.klt}")
    private String klt;
    @Value("${stockKLine.fqt}")
    private String fqt;
    @Value("${stockKLine.end}")
    private String end;
    @Value("${stockKLine.lmt}")
    private String lmt;

    @Override
    public List<Data> stock(String industryCode) {
        String stockApi = "https://push2.eastmoney.com/api/qt/clist/get?np=%s&pn=%s&pz=%s&fs=b:%s&fields=%s";
        String stockKLineApi = "https://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=%s&fields2=%s&klt=%s&fqt=%s&secid=%s.%s&end=%s&lmt=%s";

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
                    if ((code.startsWith("60") || code.startsWith("00")) && !name.contains("ST")) {//排除退市股、创业板和科创板股票(暂无权限)
                        String type = "1";
                        if (code.startsWith("00")) {
                            type = "0";
                        }
                        String formatStockKLineApi = String.format(stockKLineApi, fields1, fields2, klt, fqt, type, code, end, lmt);
                        String stockWeb = webUtil.getWeb(formatStockKLineApi);
                        JsonNode kline = objectMapper.readTree(stockWeb).get("data").get("klines");
                        if (kline != null && kline.size() > 0) {//排除新股还未上市的情况
                            List<BigDecimal> tradingVolume = new ArrayList<>();//成交量
                            List<BigDecimal> tradingAmount = new ArrayList<>();//成交额
                            for (JsonNode jsonNode : kline) {
                                tradingVolume.add(new BigDecimal(jsonNode.asText().split(",")[0]));
                                tradingAmount.add(new BigDecimal(jsonNode.asText().split(",")[1]));
                            }
                            BigDecimal tradingVolumeSum = tradingVolume.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                            BigDecimal tradingVolumeAvg = tradingVolumeSum.divide(new BigDecimal(1_0000)).divide(new BigDecimal(tradingVolume.size()), 2, RoundingMode.HALF_UP);
                            BigDecimal tradingAmountSum = tradingAmount.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
                            BigDecimal tradingAmountAvg = tradingAmountSum.divide(new BigDecimal(1_0000_0000)).divide(new BigDecimal(tradingAmount.size()), 2, RoundingMode.HALF_UP);
                            Data data = new Data();
                            data.setStockCode(code);
                            data.setStockName(name);
                            data.setTradingVolumeAvg(tradingVolumeAvg);
                            data.setTradingAmountAvg(tradingAmountAvg);
                            data.setScore(0);
                            data.setLine("<a href='https://quote.eastmoney.com/concept/" + (code.startsWith("60") ? "sh" : "sz") + code + ".html' target='_blank' style='color: red'>查看</a>");
                            data.setProfit("<a href='https://www.iwencai.com/unifiedwap/result?w=" + code + "收盘获利' target='_blank' style='color: blue'>查看</a>");
                            dataList.add(data);
                        }
                    }
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


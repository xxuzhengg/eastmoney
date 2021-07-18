package com.stock.spider.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.spider.service.StockService;
import com.stock.spider.utils.WebUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Service
public class StockServiceImpl implements StockService {
    @Resource
    TaskExecutor taskExecutor;

    @Resource
    WebUtil webUtil;

    @Resource
    ObjectMapper objectMapper;

    @Value("${stock.np}")
    private String np;
    @Value("${stock.pn}")
    private String pn;
    @Value("${stock.pz}")
    private String pz;
    @Value("${stock.fs}")
    private String fs;
    @Value("${stock.fields}")
    private String fields;

    @Value("${stockKLineApi.fields1}")
    private String fields1;
    @Value("${stockKLineApi.fields2}")
    private String fields2;
    @Value("${stockKLineApi.klt}")
    private String klt;
    @Value("${stockKLineApi.fqt}")
    private String fqt;
    @Value("${stockKLineApi.end}")
    private String end;
    @Value("${stockKLineApi.lmt}")
    private String lmt;

    @Override
    public Map<Integer, String> stock(String industryCode) {
        String stockApi = "https://push2.eastmoney.com/api/qt/clist/get?np=%s&pn=%s&pz=%s&fs=%s:%s&fields=%s";
        String stockKLineApi = "https://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=%s&fields2=%s&klt=%s&fqt=%s&secid=%s.%s&end=%s&lmt=%s";
        String mainForceApi = "https://datainterface3.eastmoney.com/EM_DataCenter_V3/api/GGZLSJTJ/GetGGZLSJTJ?tkn=eastmoney&reportDate=2021-03-31&code=%s.%s&cfg=ggzlsjtj";

        Map<Integer, String> concurrentHashMap = new ConcurrentHashMap<>();

        String formatStockApi = String.format(stockApi, np, pn, pz, fs, industryCode, fields);
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
                        StringBuilder value = new StringBuilder();
                        value.append(code).append(",").append(name);

                        String type = "1";
                        String codeType = "sh";
                        if (code.startsWith("00")) {
                            type = "0";
                            codeType = "sz";
                        }

                        //String formatStockKLineApi = String.format(stockKLineApi, fields1, fields2, klt, fqt, type, code, end, lmt);
                        //String stockWeb = webUtil.getWeb(formatStockKLineApi);
                        //JsonNode kline = objectMapper.readTree(stockWeb).get("data").get("klines");

                        String formatMainForceApi = String.format(mainForceApi, code, codeType);
                        String mainForceWeb = webUtil.getWeb(formatMainForceApi);
                        JsonNode jsonNode = objectMapper.readTree(mainForceWeb);
                        String s = jsonNode.get("Data").get(0).get("Data").get(6).asText();
                        int mainForce = Integer.parseInt(s.split("\\|")[2]);

                        if (mainForce >= 40) {
                            concurrentHashMap.put(mainForce, value.toString());
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

        Map<Integer, String> result = new LinkedHashMap<>();
        concurrentHashMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }
}


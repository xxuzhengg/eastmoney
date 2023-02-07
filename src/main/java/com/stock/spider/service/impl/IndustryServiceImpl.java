package com.stock.spider.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.spider.service.IndustryService;
import com.stock.spider.utils.RedisUtil;
import com.stock.spider.utils.WebUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Service
public class IndustryServiceImpl implements IndustryService {

    @Resource
    TaskExecutor taskExecutor;

    @Resource
    WebUtil webUtil;

    @Resource
    RedisUtil redisUtil;

    @Resource
    ObjectMapper objectMapper;

    /**
     * industryApi
     */
    @Value("${industry.np}")
    private String np;
    @Value("${industry.pn}")
    private String pn;
    @Value("${industry.pz}")
    private String pz;
    @Value("${industry.fs}")
    private String fs;
    @Value("${industry.fields}")
    private String fields;

    /**
     * industryKLineApi
     */
    @Value("${industryKLine.fields1}")
    private String fields1;
    @Value("${industryKLine.fields2}")
    private String fields2;
    @Value("${industryKLine.klt}")
    private String klt;
    @Value("${industryKLine.fqt}")
    private String fqt;
    @Value("${industryKLine.end}")
    private String end;
    @Value("${industryKLine.lmt}")
    private String lmt;

    @Override
    public void industry() {
        String industryApi = "https://push2.eastmoney.com/api/qt/clist/get?np=%s&pn=%s&pz=%s&fs=%s&fields=%s";
        String formatApi = String.format(industryApi, np, pn, pz, fs, fields);
        String web = webUtil.getWeb(formatApi);
        try {
            JsonNode jsonNode = objectMapper.readTree(web);
            int total = Integer.parseInt(jsonNode.get("data").get("total").asText());
            int size = redisUtil.getKeysByString("*").size();
            if (total != size) {
                JsonNode node = jsonNode.get("data").get("diff");
                for (JsonNode industry : node) {
                    String code = industry.get("f12").asText();
                    String name = industry.get("f14").asText();
                    redisUtil.setByString(code, name);
                }
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<BigDecimal, String> industryKLine() {
        String industryKLineApi = "https://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=%s&fields2=%s&klt=%s&fqt=%s&secid=90.%s&end=%s&lmt=%s";
        Set<String> industrySet = redisUtil.getKeysByString("*");
        Map<BigDecimal, String> concurrentHashMap = new ConcurrentHashMap<>();
        CountDownLatch countDownLatch = new CountDownLatch(industrySet.size());
        for (String industryCode : industrySet) {
            taskExecutor.execute(() -> {
                List<BigDecimal> list = new ArrayList<>();
                StringBuilder sb = new StringBuilder();
                String formatApi = String.format(industryKLineApi, fields1, fields2, klt, fqt, industryCode, end, lmt);
                String web = webUtil.getWeb(formatApi);
                try {
                    JsonNode jsonNode = objectMapper.readTree(web);
                    JsonNode node = jsonNode.get("data").get("klines");
                    for (JsonNode kline : node) {
                        list.add(new BigDecimal(kline.asText()));
                    }
                    BigDecimal min = list.stream().min(BigDecimal::compareTo).get();
                    BigDecimal now = list.get(list.size() - 1);
                    BigDecimal divide = now.subtract(min).divide(min, 6, RoundingMode.HALF_UP);
                    sb.append(industryCode).append(",")
                            .append(redisUtil.getValueByString(industryCode)).append(",")
                            .append(String.format("%.2f", divide.multiply(new BigDecimal(100)))).append("%");
                    concurrentHashMap.put(divide, sb.toString());
                } catch (JsonProcessingException e) {
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

        Map<BigDecimal, String> result = new LinkedHashMap<>();
        concurrentHashMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));

        return result;
    }
}

package com.stock.spider.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.spider.entity.Data;
import com.stock.spider.service.IndustryService;
import com.stock.spider.utils.RedisUtil;
import com.stock.spider.utils.WebUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
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
    @Value("${industryKLine.fqt}")
    private String fqt;
    @Value("${industryKLine.end}")
    private String end;
    @Value("${industryKLine.lmt}")
    private String lmt;

    /**
     * k线类型 (101日线 102周线 103月线 104季线 105半年线 106年线)
     */
    private final List<String> kLineTypeList = Arrays.asList("101", "102", "103", "104", "105", "106");

    @Override
    public void industry() {
        String industryApi = "https://push2.eastmoney.com/api/qt/clist/get?np=%s&pn=%s&pz=%s&fs=%s&fields=%s";
        String formatApi = String.format(industryApi, np, pn, pz, fs, fields);
        String web = webUtil.getWeb(formatApi);
        try {
            JsonNode jsonNode = objectMapper.readTree(web);
            JsonNode node = jsonNode.get("data").get("diff");
            for (JsonNode industry : node) {
                String code = industry.get("f12").asText();
                String name = industry.get("f14").asText();
                redisUtil.setByString(code, name);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Data> industryKLine() {
        String industryKLineApi = "https://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=%s&fields2=%s&klt=%s&fqt=%s&secid=90.%s&end=%s&lmt=%s";
        Set<String> industrySet = redisUtil.getKeysByString("*");
        List<Data> dataList = new CopyOnWriteArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(industrySet.size());
        for (String industryCode : industrySet) {
            taskExecutor.execute(() -> {
                try {
                    List<BigDecimal> increaseList = new ArrayList<>();
                    for (String klt : kLineTypeList) {
                        String formatApi = String.format(industryKLineApi, fields1, fields2, klt, fqt, industryCode, end, lmt);
                        String web = webUtil.getWeb(formatApi);
                        JsonNode jsonNode = objectMapper.readTree(web);
                        BigDecimal increase = new BigDecimal(jsonNode.get("data").get("klines").get(0).asText());
                        increaseList.add(increase);
                    }
                    Data data = new Data();
                    data.setIndustryCode(industryCode);
                    data.setIndustryName(redisUtil.getValueByString(industryCode));
                    data.setLine("<a href='https://quote.eastmoney.com/bk/90." + industryCode + ".html' target='_blank' style='color: red'>查看</a>");
                    data.setDayIncrease(increaseList.get(0));
                    data.setWeekIncrease(increaseList.get(1));
                    data.setMonthIncrease(increaseList.get(2));
                    data.setQuarterIncrease(increaseList.get(3));
                    data.setHalfYearIncrease(increaseList.get(4));
                    data.setYearIncrease(increaseList.get(5));
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

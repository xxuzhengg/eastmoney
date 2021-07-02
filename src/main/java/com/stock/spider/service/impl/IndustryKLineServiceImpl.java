package com.stock.spider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.stock.spider.service.IndustryKLineService;
import com.stock.spider.utils.RedisUtil;
import com.stock.spider.utils.WebUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class IndustryKLineServiceImpl implements IndustryKLineService {
    @Resource
    TaskExecutor taskExecutor;

    @Resource
    WebUtil webUtil;

    @Resource
    RedisUtil redisUtil;

    @Value("${industryKLine.fields1}")
    private String fields1;
    @Value("${industryKLine.fields2}")
    private String fields2;
    @Value("${industryKLine.klt}")
    private String klt;
    @Value("${industryKLine.fqt}")
    private String fqt;
    @Value("${industryKLine.type}")
    private String type;
    @Value("${industryKLine.end}")
    private String end;

    @Override
    public void industryKLine() {
        String industryKLineApi = "https://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=%s&fields2=%s&klt=%s&fqt=%s&secid=%s.%s&end=%s&lmt=%s";

        //从15年股灾后开始算起,2016-02-01
        long limit = LocalDate.parse("2016-02-01").until(LocalDate.now(), ChronoUnit.MONTHS) + 1;

        redisUtil.selectDataBase(0);

        Set<String> industrySet = redisUtil.getKeysByString("*");
        //需要注意的是,专用设备BK0910,最早是从2016-07-15开始,暂时不考虑
        List<String> industryList = industrySet.stream().filter(e -> !e.equals("BK0910")).collect(Collectors.toList());

        redisUtil.selectDataBase(1);

        for (String industryCode : industryList) {
            taskExecutor.execute(() -> {
                Map<String, String> hashMap = new HashMap<>();
                String formatApi = String.format(industryKLineApi, fields1, fields2, klt, fqt, type, industryCode, end, limit);
                String web = webUtil.getWeb(formatApi);
                JSONArray jsonArray = JSON.parseObject(web).getJSONObject("data").getJSONArray("klines");
                for (Object object : jsonArray) {
                    String date = object.toString().split(",")[0];
                    if (date.contains("2021")) break;

                    StringBuilder key = new StringBuilder();
                    LocalDate localDate = LocalDate.parse(date);
                    key.append(localDate.getYear()).append("-").append(localDate.getMonthValue());

                    double quote = Double.parseDouble(object.toString().split(",")[1]);

                    hashMap.put(key.toString(), String.valueOf(quote));
                }
                redisUtil.setByHash(industryCode, hashMap);
            });
        }
    }
}

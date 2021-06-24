package com.stock.spider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.stock.spider.service.StockService;
import com.stock.spider.utils.WebUtil;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private final String fields1 = "f1,f2,f3,f4,f5,f6";
    private final String fields2 = "f53,f57";
    private final String klt = "101";
    private final String fqt = "1";
    private final String beg = "0";
    private final String end = "20500000";

    @Override
    public void stock(String industryCode) {
        String stockApi = "http://80.push2.eastmoney.com/api/qt/clist/get?pn=1&pz=500&po=1&np=1&fltt=2&invt=2&fid=f3&fs=b:" + industryCode + "+f:!50&fields=f12,f14";
        String stockKLineApi = "https://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=%s&fields2=%s&klt=%s&fqt=%s&secid=%s.%s&beg=%s&end=%s";

        Map<Double, String> concurrentHashMap = new ConcurrentHashMap<>();

        String web = webUtil.getWeb(stockApi);
        JSONArray jsonArray = JSON.parseObject(web).getJSONObject("data").getJSONArray("diff");

        CountDownLatch countDownLatch = new CountDownLatch(jsonArray.size());

        for (Object object : jsonArray) {
            taskExecutor.execute(() -> {
                String code = JSON.parseObject(object.toString()).get("f12").toString();
                String name = JSON.parseObject(object.toString()).get("f14").toString();
                if ((code.startsWith("60") || code.startsWith("00")) && !name.contains("ST")) {//排除退市股、创业板和科创板股票(暂无权限)
                    StringBuilder value = new StringBuilder();
                    value.append(code).append(",").append(name);

                    String type = "1";
                    if (code.startsWith("00")) {
                        type = "0";
                    }

                    Double sumPrice = 0.0;//元
                    Double avgPrice;//元
                    Double sumAmount = 0.0;//亿
                    Double avgAmount;//亿

                    String formatApi = String.format(stockKLineApi, fields1, fields2, klt, fqt, type, code, beg, end);
                    String stockWeb = webUtil.getWeb(formatApi);
                    JSONArray stockArray = JSON.parseObject(stockWeb).getJSONObject("data").getJSONArray("klines");
                    int size = stockArray.size();
                    int limit = size > 40 ? size - 40 : 0;//2个月 1个月20个交易日
                    int avg = 0;
                    for (int i = size - 1; i >= limit; i--) {
                        String[] split = stockArray.get(i).toString().split(",");
                        String price = split[0];
                        String amount = split[1];
                        sumPrice += Double.parseDouble(price);
                        sumAmount += Double.parseDouble(amount);
                        avg++;
                    }

                    avgPrice = new BigDecimal(sumPrice / avg).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    avgAmount = new BigDecimal(sumAmount / avg / 1e8).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    if (avgPrice > 10 && avgAmount > 2) {
                        concurrentHashMap.put(avgAmount, value.toString());
                    }
                }
                countDownLatch.countDown();
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //股票最近2个月的平均交易额 顺序输出
        System.out.println("-----分界线-----");
        Map<Double, String> result = new LinkedHashMap<>();
        concurrentHashMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> result.put(e.getKey(), e.getValue()));
        result.entrySet().stream().forEach(e -> System.out.println(e.getKey() + "," + e.getValue()));
    }
}


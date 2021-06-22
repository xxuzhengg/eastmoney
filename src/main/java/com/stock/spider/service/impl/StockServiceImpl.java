package com.stock.spider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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

    @Override
    public void stock(String industryCode) {
        String stockApi = "http://80.push2.eastmoney.com/api/qt/clist/get?pn=1&pz=500&po=1&np=1&fltt=2&invt=2&fid=f3&fs=b:" + industryCode + "+f:!50&fields=f12,f14";
        String sizeApi = "http://push2.eastmoney.com/api/qt/stock/get?fltt=2&invt=2&secid=%s.%s&fields=f43,f47,f168";

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

                    String formatApi = String.format(sizeApi, type, code);
                    String sizeWeb = webUtil.getWeb(formatApi);
                    JSONObject sizeObject = JSON.parseObject(sizeWeb).getJSONObject("data");
                    String f43 = sizeObject.get("f43").toString();
                    String f47 = sizeObject.get("f47").toString();
                    String f168 = sizeObject.get("f168").toString();

                    double price = Double.parseDouble(f43);//股票价格
                    if (price > 10) {
                        value.append(",").append(price);
                        int turnover = Integer.parseInt(f47);//成交量(1手=100股)
                        double rate = Double.parseDouble(f168);//换手率
                        double size = turnover * 100 / rate / 100 / 10000;//股票盘子大小(流通股)(单位亿)
                        Double key = new BigDecimal(size).setScale(2, RoundingMode.HALF_UP).doubleValue();//保留两位小数

                        concurrentHashMap.put(key, value.toString());
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

        //按股票盘子大小顺序输出
        System.out.println("-----分界线-----");
        Map<Double, String> result = new LinkedHashMap<>();
        concurrentHashMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> result.put(e.getKey(), e.getValue()));
        result.entrySet().stream().forEach(e -> System.out.println(e.getKey() + "," + e.getValue()));
    }
}


package com.stock.spider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.stock.spider.service.IndustryService;
import com.stock.spider.utils.RedisUtil;
import com.stock.spider.utils.WebUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class IndustryServiceImpl implements IndustryService {
    @Resource
    TaskExecutor taskExecutor;

    @Resource
    WebUtil webUtil;

    @Resource
    RedisUtil redisUtil;

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

    @Override
    public void industry() {
        String industryApi = "https://push2.eastmoney.com/api/qt/clist/get?np=%s&pn=%s&pz=%s&fs=%s&fields=%s";
        String formatApi = String.format(industryApi, np, pn, pz, fs, fields);
        String web = webUtil.getWeb(formatApi);
        JSONArray jsonArray = JSON.parseObject(web).getJSONObject("data").getJSONArray("diff");
        redisUtil.selectDataBase(0);
        for (int i = 0; i < jsonArray.size(); i++) {
            int finalI = i;
            taskExecutor.execute(() -> {
                String code = JSON.parseObject(jsonArray.get(finalI).toString()).get("f12").toString();
                String name = JSON.parseObject(jsonArray.get(finalI).toString()).get("f14").toString();
                redisUtil.setByString(code, name);
            });
        }
    }
}

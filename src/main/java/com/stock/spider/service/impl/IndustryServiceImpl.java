package com.stock.spider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.stock.spider.service.IndustryService;
import com.stock.spider.utils.RedisUtil;
import com.stock.spider.utils.WebUtil;
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

    @Override
    public void industry() {
        String industryApi = "http://76.push2.eastmoney.com/api/qt/clist/get?pn=1&pz=100&po=1&np=1&fltt=2&invt=2&fid=f3&fs=m:90+t:2+f:!50&fields=f12,f14";
        String web = webUtil.getWeb(industryApi);
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

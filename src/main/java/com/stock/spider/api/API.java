package com.stock.spider.api;

public class API {

    /**
     * 股票k线
     * http://push2his.eastmoney.com/api/qt/stock/kline/get?beg=0&end=20500101&rtntype=6&secid=0.300337&klt=103&fqt=1
     * &fields1=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13&fields2=f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61
     * <p>
     * fields2分别对应日期,开盘,收盘,最高,最低,成交量,成交额,振幅,涨跌幅,涨跌额,换手率
     * [振幅]就是股票开盘后的当日最高价和最低价之间的差的绝对值与昨日收盘价的百分比,它在一定程度上表现股票的活跃程度
     * [换手率]表示市场中股票转手买卖的频率,是反映股票流通性强弱的指标之一
     * <p>
     * secid为股票代码
     * 若是0,3开头的股票 格式为0.股票代码(深市)
     * 若是6开头的股票 格式为1.股票代码(沪市)
     * <p>
     * klt为k线类型
     * 101为日线,102为周线,103为月线
     */

    /**
     * 行业
     * http://76.push2.eastmoney.com/api/qt/clist/get?pn=1&pz=100&po=1&np=1&fltt=2&invt=2&fid=f3&fs=m:90+t:2+f:!50&fields=f12,f14
     */

    /**
     * 行业k线
     * http://14.push2his.eastmoney.com/api/qt/stock/kline/get?secid=90.BK0477&fields1=f1,f2,f3,f4,f5,f6&fields2=f51,f59&klt=103&fqt=1&end=20500101&lmt=62
     */

    /**
     * 行业所对应的股票
     * http://80.push2.eastmoney.com/api/qt/clist/get?pn=1&pz=500&po=1&np=1&fltt=2&invt=2&fid=f3&fs=b:BK0465+f:!50&fields=f12,f14
     */

    /**
     * selenium+谷歌浏览器驱动
     * https://chromedriver.chromium.org/downloads
     * selenium API
     * https://blog.nowcoder.net/n/801d2b17f0214b51acfc7dbeeb189330
     */
}

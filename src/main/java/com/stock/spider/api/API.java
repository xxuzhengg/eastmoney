package com.stock.spider.api;

public class API {

    /**
     * 股票k线
     * https://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=%s&fields2=%s&klt=%s&fqt=%s&secid=%s.%s&beg=%s&end=%s
     *
     * fields1: f1,f2,f3,f4,f5,f6
     *
     * fields2: f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61
     * 日期,开盘,收盘,最高,最低,成交量,成交额,振幅,涨跌幅,涨跌额,换手率
     *
     * klt: k线类型
     * 101日线 102周线 103月线 104季线 105半年线 106年线
     *
     * fqt: 复权类型
     * 0不复权 1前复权 2后复权
     *
     * secid: 股票代码 如 0.002594
     * 0深股 1沪股
     *
     * beg: 0
     *
     * end: 20500000
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

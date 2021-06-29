## 一个简单的股票爬虫程序，爬的是东方财富的数据

### 环境

> jdk 11

> springboot 2.5.x + thymeleaf 3.0.x

### 说明

> 数据直接存redis，接着直接输出，没有保存mysql，也没有保存office文档

> 需要先启动redis-server.exe

### 接口

* 股票k线

> https://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=%s&fields2=%s&klt=%s&fqt=%s&secid=%s.%s&end=%s&lmt=%s

* 行业k线

> https://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=%s&fields2=%s&klt=%s&fqt=%s&secid=%s.%s&end=%s&lmt=%s

```
fields1: f1,f3 (代码,名称)
fields2: f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61 (日期,开盘,收盘,最高,最低,成交量,成交额,振幅,涨跌幅,涨跌额,换手率)
klt: k线类型 (101日线 102周线 103月线 104季线 105半年线 106年线)
fqt: 复权类型 (0不复权 1前复权 2后复权)
secid: 股票/行业代码 如股票 0.002594 (0深股 1沪股) 如行业 90.BK0733 (90是固定值)
end: 20500101
lmt: limit
```

* 行业

> https://push2.eastmoney.com/api/qt/clist/get?np=%s&pn=%s&pz=%s&fs=%s&fields=%s

* 股票

> https://push2.eastmoney.com/api/qt/clist/get?np=%s&pn=%s&pz=%s&fs=%s&fields=%s

```
np: 1 (jsonArray格式)
pn: 1 (offset)
pz: 行业100 股票500 (limit)
fs: 行业代码 m:90+t:2 股票代码 b:BK0477
fields: f12,f14 (代码,名称)
```

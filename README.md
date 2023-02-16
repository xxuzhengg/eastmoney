## 一个简单的股票爬虫程序，爬的是东方财富的数据

### 环境

> jdk 17

> springboot 2.5.x + thymeleaf 3.0.x

### 说明

> 下载客户端，然后启动redis-server.exe  
> https://github.com/tporadowski/redis/tags

> 爬行业种类数 -> 爬各行业k线   
> 爬各行业下对应的股票 -> 爬各股票k线

> 如果接口调用时候处于盘前/后，则接口返回的最新那一条数据是上一个交易日的  
> 如果接口调用时候处于盘中，则接口返回的最新那一条数据是今天的实时数据

> 获取行业信息 -> `涨幅` -> 当天值与最低值作比较，保留两位小数，符号是%  
> 获取行业对应的股票信息 -> `个股评分` -> 个股涨幅与对应行业涨幅的差值的和

### 接口

* 某一股票k线

> https://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=%s&fields2=%s&klt=%s&fqt=%s&secid=%s.%s&end=%s&lmt=%s

* 某一行业k线

> https://push2his.eastmoney.com/api/qt/stock/kline/get?fields1=%s&fields2=%s&klt=%s&fqt=%s&secid=%s.%s&end=%s&lmt=%s

```
fields1: f1,f3 (代码,名称)
fields2: f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61 (日期,开盘,收盘,最高,最低,成交量,成交额,振幅,涨跌幅,涨跌额,换手率)
klt: k线类型 (101日线 102周线 103月线 104季线 105半年线 106年线)
fqt: 复权类型 (0不复权 1前复权 2后复权)
secid: 股票/行业代码 如股票 0.002594 (0深股 1沪股) 如行业 90.BK0733 (90是固定值)
end: 20500101 (非固定值 可以更大或者再小一点 也不知道干啥的)
lmt: 965 (limit)
```

* 行业列表

> https://push2.eastmoney.com/api/qt/clist/get?np=%s&pn=%s&pz=%s&fs=%s&fields=%s

* 某一行业下的股票列表

> https://push2.eastmoney.com/api/qt/clist/get?np=%s&pn=%s&pz=%s&fs=%s&fields=%s

```
np: 1 (jsonArray格式)
pn: 1 (offset)
pz: 965 (limit)
fs: 股票/行业代码 如股票 b:BK0477 (b:是固定值) 如行业 m:90+t:2 (固定值)
fields: f12,f14 (代码,名称)
```

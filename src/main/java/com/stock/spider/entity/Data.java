package com.stock.spider.entity;

import java.math.BigDecimal;

public class Data {
    int id;//编号
    String stockCode;//股票代码
    String stockName;//股票名称
    BigDecimal tradingVolumeAvg;//日均成交量(万)
    BigDecimal tradingAmountAvg;//日均成交额(亿)
    int score;//个股跟着行业，同涨同跌得0分，逆涨得-1分，逆跌得1分
    String line;//k线图
    String profit;//获利盘

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public BigDecimal getTradingVolumeAvg() {
        return tradingVolumeAvg;
    }

    public void setTradingVolumeAvg(BigDecimal tradingVolumeAvg) {
        this.tradingVolumeAvg = tradingVolumeAvg;
    }

    public BigDecimal getTradingAmountAvg() {
        return tradingAmountAvg;
    }

    public void setTradingAmountAvg(BigDecimal tradingAmountAvg) {
        this.tradingAmountAvg = tradingAmountAvg;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }
}

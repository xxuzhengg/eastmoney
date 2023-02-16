package com.stock.spider.entity;

import java.math.BigDecimal;

public class Data {

    /**
     * 获取行业信息(最近半年)
     */
    String industryCode;//行业代码
    String industryName;//行业名称
    BigDecimal increase;//涨幅(当天值与最低值)
    String line;//k线图(第三方网站链接)


    /**
     * 获取行业对应的股票信息(最近一个月)
     */
    String stockCode;//股票代码
    String stockName;//股票名称
    BigDecimal tradingVolumeAvg;//日均成交量(万)
    BigDecimal tradingAmountAvg;//日均成交额(亿)
    int score;//个股涨幅与对应行业涨幅的差值的和
    String profit;//获利盘(第三方网站链接)

    public String getIndustryCode() {
        return industryCode;
    }

    public void setIndustryCode(String industryCode) {
        this.industryCode = industryCode;
    }

    public String getIndustryName() {
        return industryName;
    }

    public void setIndustryName(String industryName) {
        this.industryName = industryName;
    }

    public BigDecimal getIncrease() {
        return increase;
    }

    public void setIncrease(BigDecimal increase) {
        this.increase = increase;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
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

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }
}

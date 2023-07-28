package com.stock.spider.entity;

import java.math.BigDecimal;

public class Data {

    /**
     * 获取行业信息
     */
    String industryCode;//行业代码
    String industryName;//行业名称
    BigDecimal dayIncrease;//日涨幅(%)
    BigDecimal weekIncrease;//周涨幅(%)
    BigDecimal monthIncrease;//月涨幅(%)
    BigDecimal quarterIncrease;//季涨幅(%)
    BigDecimal halfYearIncrease;//半年涨幅(%)
    BigDecimal yearIncrease;//年涨幅(%)
    String line;//k线图(第三方网站链接)


    /**
     * 获取行业对应的股票信息
     */
    String stockCode;//股票代码
    String stockName;//股票名称
    BigDecimal tradingVolumeAvg;//日均成交量(万)
    BigDecimal tradingAmountAvg;//日均成交额(亿)
    BigDecimal score;//个股涨幅与对应行业涨幅的差值
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

    public BigDecimal getDayIncrease() {
        return dayIncrease;
    }

    public void setDayIncrease(BigDecimal dayIncrease) {
        this.dayIncrease = dayIncrease;
    }

    public BigDecimal getWeekIncrease() {
        return weekIncrease;
    }

    public void setWeekIncrease(BigDecimal weekIncrease) {
        this.weekIncrease = weekIncrease;
    }

    public BigDecimal getMonthIncrease() {
        return monthIncrease;
    }

    public void setMonthIncrease(BigDecimal monthIncrease) {
        this.monthIncrease = monthIncrease;
    }

    public BigDecimal getQuarterIncrease() {
        return quarterIncrease;
    }

    public void setQuarterIncrease(BigDecimal quarterIncrease) {
        this.quarterIncrease = quarterIncrease;
    }

    public BigDecimal getHalfYearIncrease() {
        return halfYearIncrease;
    }

    public void setHalfYearIncrease(BigDecimal halfYearIncrease) {
        this.halfYearIncrease = halfYearIncrease;
    }

    public BigDecimal getYearIncrease() {
        return yearIncrease;
    }

    public void setYearIncrease(BigDecimal yearIncrease) {
        this.yearIncrease = yearIncrease;
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

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
        this.profit = profit;
    }
}

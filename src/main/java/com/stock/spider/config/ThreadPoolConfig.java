package com.stock.spider.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池规则
 */
@Configuration
public class ThreadPoolConfig {
    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        //获取当前系统cpu核心数
        int cpu = Runtime.getRuntime().availableProcessors();
        //核心线程数: 线程池初始化后的初始数量
        taskExecutor.setCorePoolSize(cpu);
        //最大线程数: 超过核心线程数+阻塞队列满了 则新建线程直到达到最大线程数
        taskExecutor.setMaxPoolSize(cpu * 2);
        //阻塞队列长度: 超过核心线程数就将任务放进队列中
        taskExecutor.setQueueCapacity(cpu * 3);
        //过期时间: 超过核心线程数的线程如果闲置时间超过这个数 则被销毁
        taskExecutor.setKeepAliveSeconds(60);
        //线程名称前缀
        taskExecutor.setThreadNamePrefix("stock-spider-");
        //优雅的关闭线程 全部线程都执行完任务后才关闭
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        //任务数量如果超过 最大线程数+阻塞队列长度 则表示线程池满了 执行这个饱和策略 即使用当前线程执行 比如 main线程
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //初始化
        taskExecutor.initialize();
        return taskExecutor;
    }
}

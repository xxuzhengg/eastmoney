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
        int cpu = Runtime.getRuntime().availableProcessors();
        //设置线程池参数信息
        taskExecutor.setCorePoolSize(cpu);
        taskExecutor.setMaxPoolSize(cpu * 2);
        taskExecutor.setQueueCapacity(300);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setThreadNamePrefix("stock-spider-");
        taskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        taskExecutor.setAwaitTerminationSeconds(60);
        //修改拒绝策略为使用当前线程执行
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //初始化线程池
        taskExecutor.initialize();
        return taskExecutor;
    }
}

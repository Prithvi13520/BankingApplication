package com.app.banking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);  // minimum number of threads
        executor.setMaxPoolSize(10);  // maximum number of threads
        executor.setQueueCapacity(25);  // queue size for tasks waiting to be executed
        executor.setThreadNamePrefix("Async-");  // thread name prefix
        executor.initialize();
        return executor;
    }
}


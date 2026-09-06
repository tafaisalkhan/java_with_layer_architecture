package com.mycloud.orchestratorservice.config;

import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class OperationExecutorConfiguration {
    @Bean(name = "operationExecutor")
    public Executor operationExecutor(@Value("${app.scheduler.worker-threads:10}") int workerThreads) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(workerThreads);
        executor.setMaxPoolSize(workerThreads);
        executor.setQueueCapacity(workerThreads * 10);
        executor.setThreadNamePrefix("operation-worker-");
        executor.initialize();
        return executor;
    }
}

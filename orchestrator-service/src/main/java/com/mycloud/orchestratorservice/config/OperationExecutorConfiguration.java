package com.mycloud.orchestratorservice.config;

import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class OperationExecutorConfiguration {
    @Bean(name = "provisioningExecutor")
    public Executor provisioningExecutor(@Value("${app.scheduler.pending.worker-threads:10}") int workerThreads) {
        return executor(workerThreads, "provisioning-worker-");
    }

    @Bean(name = "pollingExecutor")
    public Executor pollingExecutor(@Value("${app.scheduler.polling.worker-threads:20}") int workerThreads) {
        return executor(workerThreads, "polling-worker-");
    }

    private Executor executor(int workerThreads, String threadNamePrefix) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(workerThreads);
        executor.setMaxPoolSize(workerThreads);
        executor.setQueueCapacity(workerThreads * 10);
        executor.setThreadNamePrefix(threadNamePrefix);
        executor.initialize();
        return executor;
    }
}

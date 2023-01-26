package com.example.notification_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@EnableAsync
public class TaskExecutorConfig {

    @Value("${task_custom.execution.pool.core-size}")
    private int corePoolSize;

    @Value("${task_custom.execution.pool.max-size}")
    private int maxPoolSize;

    @Value("${task_custom.execution.pool.queue-capacity}")
    private int queueCapacity;

    @Value("${task_custom.execution.shutdown.await-termination}")
    private boolean waitForTasksToCompleteOnShutdown;

    @Bean
    public TaskExecutor taskExecutorCustom() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(waitForTasksToCompleteOnShutdown);
        executor.initialize();
        return executor;
    }

}

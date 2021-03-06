package com.hummer.pipeline.plugin;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: lee
 * @version:1.0.0
 * @Date: 2018/12/10 18:49
 **/
@Configuration
public class TaskThreadPoolBean {

    /**
     * 初始化任务task线程池。
     * <pre>
     *     1.线程池初始化大小:默认线程池大小为CPU逻辑数
     *     2.最大线程数为CPU逻辑数*2
     * </pre>
     *
     * @param
     * @return java.util.concurrent.ExecutorService
     * @author liguo
     * @date 2018/12/11 13:31
     * @version 1.0.0
     **/
    @Bean(name = "defaultTaskGroup")
    @Lazy
    public ListeningExecutorService initTaskGroupV1ThreadPool() {
        int size = 1000 /**BaseProperties.getProperty("task-queue-max-size"
                , Integer.class
                , 1000)**/;

        ThreadFactory tf =
                new ThreadFactoryBuilder()
                        .setNameFormat(String.format("%s-Th"
                                , /**BaseProperties.getProperty("task-queue-biz-name"
                                        , String.class
                                        , "defaultTaskGroup")
                                        + "-%d"*/1))
                        .setDaemon(true)
                        .build();

        ExecutorService executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()
                , Runtime.getRuntime().availableProcessors() * 2
                , 0
                , TimeUnit.MILLISECONDS
                , new LinkedBlockingQueue<>(size)
                , tf
                , new ThreadPoolExecutor.CallerRunsPolicy());

        return MoreExecutors.listeningDecorator(executor);
    }

    /**
     * 初始化任务task线程池。
     * <pre>
     *     1.线程池初始化大小:默认线程池大小为CPU逻辑数
     *     2.最大线程数为CPU逻辑数*2
     * </pre>
     *
     * @param
     * @return java.util.concurrent.ExecutorService
     * @author liguo
     * @date 2018/12/11 13:31
     * @version 1.0.0
     **/
    @Bean(name = "defaultTaskGroupV2")
    @Lazy
    public ExecutorService initTaskGroupV2ThreadPool() {
        int size = 1000/**BaseProperties.getProperty("task-queue-max-size"
                , Integer.class
                , 1000)**/;

        ThreadFactory tf =
                new ThreadFactoryBuilder()
                        .setNameFormat(String.format("%s-Th"
                                , /**BaseProperties.getProperty("task-queue-biz-name"
                                        , String.class
                                        , "defaultTaskGroupV2")
                                        + "-%d"**/1))
                        .setDaemon(true)
                        .build();

        final MdcTaskDecorator taskDecorator = new MdcTaskDecorator();

        return new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()
                , Runtime.getRuntime().availableProcessors() * 2
                , 0
                , TimeUnit.MILLISECONDS
                , new LinkedBlockingQueue<>(size)
                , tf
                , new ThreadPoolExecutor.CallerRunsPolicy()) {
            @Override
            public void execute(Runnable command) {
                super.execute(taskDecorator.decorate(command));
            }
        };
    }


    /**
     * 初始化一个固定线程
     * <pre>
     *     适合固定顺序执行任务场景
     * </pre>
     *
     * @param
     * @return java.util.concurrent.ExecutorService
     * @author liguo
     * @date 2018/12/17 11:01
     * @version 1.0.0
     **/
    @Bean(name = "defaultTaskOenThreadGroupV2")
    @Lazy
    public ExecutorService initTaskOenThreadGroupV2ThreadPool() {
        int size =1000; /**BaseProperties.getProperty("task-queue-max-size"
                , Integer.class
                , 1000)**/;

        ThreadFactory tf =
                new ThreadFactoryBuilder()
                        .setNameFormat(String.format("%s-Th"
                                , /**BaseProperties.getProperty("task-queue-biz-name"
                                        , String.class
                                        , "defaultTaskOenThreadGroupV2")
                                        + "-%d"**/1))
                        .setDaemon(true)
                        .build();

        final MdcTaskDecorator taskDecorator = new MdcTaskDecorator();

        return new ThreadPoolExecutor(1
                , 1
                , 0
                , TimeUnit.MILLISECONDS
                , new LinkedBlockingQueue<>(size)
                , tf
                , new ThreadPoolExecutor.CallerRunsPolicy()) {
            @Override
            public void execute(Runnable command) {
                super.execute(taskDecorator.decorate(command));
            }
        };
    }


    /**
     * 初始化任务task线程池。
     * <pre>
     *     1.线程池初始化大小:默认线程池大小为CPU逻辑数
     *     2.最大线程数为CPU逻辑数*2
     *     3.使用spring 框架ThreadPoolTaskExecutor
     * </pre>
     *
     * @param
     * @return java.util.concurrent.ExecutorService
     * @author liguo
     * @date 2018/12/11 13:31
     * @version 1.0.0
     **/
    @Bean(name = "defaultTaskGroupV3")
    @Lazy
    public ThreadPoolTaskExecutor initTaskGroupV3ThreadPool() {

        ThreadFactory tf =
                new ThreadFactoryBuilder()
                        .setNameFormat(String.format("%s-Th"
                                , /**BaseProperties.getProperty("task-queue-biz-name"
                                        , String.class
                                        , "defaultTaskGroupV3")
                                        + "-%d"**/1))
                        .setDaemon(true)
                        .build();

        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        taskExecutor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 2);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setTaskDecorator(new MdcTaskDecorator());
        taskExecutor.setQueueCapacity(/**BaseProperties.getProperty("task-queue-max-size"
                , Integer.class
                , 1000)**/1);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        taskExecutor.setThreadFactory(tf);

        return taskExecutor;
    }
}

package org.chw.rpc.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 创建线程池的工具类
 *
 * @Author CHW
 * @Date 2023/4/21
 **/
public class ThreadPoolFactory {
    private static final int CORE_POOL_SIZE = 10;
    private static final int MAXIMUM_POOL_SIZE_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final int BLOCKING_QUEUE_CAPACITY = 100;
    
    private final static Logger logger = LoggerFactory.getLogger(ThreadPoolFactory.class);
    
    private static Map<String, ExecutorService> threadPollsMap = new ConcurrentHashMap<>();
    
    private ThreadPoolFactory() {
    }
    
    public static ExecutorService createDefaultThreadPool(String threadNamePrefix) {
        return createDefaultThreadPool(threadNamePrefix, false);
    }
    
    /**
     * 获取线程池
     */
    public static ExecutorService createDefaultThreadPool(String threadNamePrefix , Boolean daemon) {
        //如果threadNamePrefix线程池未被创建则创建并加入map
        ExecutorService pool = threadPollsMap.computeIfAbsent(threadNamePrefix , k -> createThreadPool(threadNamePrefix , daemon));
        //如果线程池已经被关闭则重新创建
        if (pool.isShutdown() || pool.isTerminated()){
            threadPollsMap.remove(threadNamePrefix);
            pool = createThreadPool(threadNamePrefix ,daemon);
            threadPollsMap.put(threadNamePrefix , pool);
        }
        return pool;
    }
    
    public static ExecutorService createThreadPool(String threadNamePrefix, Boolean daemon) {
        // 使用有界队列
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(BLOCKING_QUEUE_CAPACITY);
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE_SIZE, KEEP_ALIVE_TIME, TimeUnit.MINUTES, workQueue, threadFactory);
    }
    
    
    /**
     * 创建 ThreadFactory 。如果ThreadNamePrefix为null则使用Executors.defaultThreadFactory创建默认的线程池。
     * 如果不为null则根据threadNamePrefix设置线程的前缀，daemon为true则为守护线程。
     *
     * @param threadNamePrefix 作为创建的线程名字的前缀
     * @param daemon           指定是否为 Daemon Thread(守护线程)
     * @return ThreadFactory
     */
    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        
        return Executors.defaultThreadFactory();
    }
    
    /**
     * 关闭所有线程池
     */
    public static void shutDownAll() {
        logger.info("关闭所有线程池...");
        //遍历关闭
        threadPollsMap.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            logger.info("关闭线程池 [{}] [{}]", entry.getKey(), executorService.isTerminated());
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException ie) {
                logger.error("关闭线程池失败！");
                executorService.shutdownNow();
            }
        });
    }
    
}

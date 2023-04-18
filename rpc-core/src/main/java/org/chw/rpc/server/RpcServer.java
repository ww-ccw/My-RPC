package org.chw.rpc.server;

import org.chw.rpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * @Author CHW
 * @Date 2023/4/17
 *
 * 远程方法的服务提供者（服务端）
 **/
public class RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(RpcServer.class);
    
    private final int CORE_POOL_SIZE = 5;
    private final int MAXIMUM_POOL_SIZE = 50;
    private final long KEEP_ALIVE_TIME = 600;
    private final int QUEUE_SIZE = 100;
    
    //线程池
    private final ExecutorService threadPool;
    //服务注册表
    private final ServiceRegistry serviceRegistry;
    
    public RpcServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE , MAXIMUM_POOL_SIZE , KEEP_ALIVE_TIME , TimeUnit.SECONDS , workingQueue , threadFactory);
    }
    
    /**
     * 等待到一个请求就开启一个工作线程处理
     * @param port 监听端口号
     */
    public void start(int port){
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器正在启动...");
            Socket socket ;
            while((socket = serverSocket.accept()) != null){
                logger.info("客户端连接!\tIP为"+socket.getInetAddress() + ":" + socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket , serviceRegistry));
            }
        } catch (IOException e) {
            logger.error("连接时有错误发生:" , e);
        }
    }
}

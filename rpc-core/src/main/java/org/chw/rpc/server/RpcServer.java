package org.chw.rpc.server;

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
    //线程池
    private final ExecutorService threadPool;
    
    public RpcServer() {
        int corePoolSize = 5;
        int maximumPoolSize = 50;
        long keepAliveTime = 600;
        BlockingQueue<Runnable> workingQueue = new ArrayBlockingQueue<>(100);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.threadPool = new ThreadPoolExecutor(corePoolSize , maximumPoolSize , keepAliveTime , TimeUnit.SECONDS , workingQueue , threadFactory);
    }
    
    /**
     * 等待到一个请求就开启一个工作线程处理
     * @param service 服务的实例地址
     * @param port 监听端口号
     */
    public void register(Object service , int port){
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器正在启动...");
            Socket socket ;
            while((socket = serverSocket.accept()) != null){
                logger.info("客户端连接!\tIP为"+socket.getInetAddress() + ":" + socket.getPort());
                threadPool.execute(new RequestHandler(socket , service));
            }
        } catch (IOException e) {
            logger.error("连接时有错误发生:" , e);
        }
    }
}

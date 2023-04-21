package org.chw.rpc.socket.server;

import org.chw.rpc.RpcServer;
import org.chw.rpc.enumeration.RpcError;
import org.chw.rpc.exception.RpcException;
import org.chw.rpc.registry.ServiceRegistry;
import org.chw.rpc.serializer.CommonSerializer;
import org.chw.rpc.util.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * 远程方法的服务提供者（服务端）
 * 监听端口，接受请求
 * @Author CHW
 * @Date 2023/4/17
 **/
public class SocketServer implements RpcServer {
    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);
    
    //线程池
    private final ExecutorService threadPool;
    //服务注册表
    private final ServiceRegistry serviceRegistry;
    //序列化器
    private CommonSerializer serializer;
    
    public SocketServer(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
    }
    
    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
    
    /**
     * 等待到一个请求就开启一个工作线程处理
     * @param port 监听端口号
     */
    public void start(int port){
    
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器正在启动...");
            Socket socket ;
            while((socket = serverSocket.accept()) != null){
                logger.info("客户端连接!\tIP为"+socket.getInetAddress() + ":" + socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket , serviceRegistry , serializer));
            }
        } catch (IOException e) {
            logger.error("连接时有错误发生:" , e);
        }
    }
}

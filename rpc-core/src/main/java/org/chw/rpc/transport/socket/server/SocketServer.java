package org.chw.rpc.transport.socket.server;


import org.chw.rpc.hook.ShutdownHook;
import org.chw.rpc.provider.ServiceProvider;
import org.chw.rpc.provider.ServiceProviderImpl;
import org.chw.rpc.registry.NacosServiceRegistry;
import org.chw.rpc.transport.AbstractRpcServer;
import org.chw.rpc.transport.RpcServer;
import org.chw.rpc.registry.ServiceRegistry;
import org.chw.rpc.serializer.CommonSerializer;
import org.chw.rpc.util.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * 远程方法的服务提供者（服务端）
 * 监听端口，接受请求
 * @Author CHW
 * @Date 2023/4/17
 **/
public class SocketServer extends AbstractRpcServer {
    //线程池
    private final ExecutorService threadPool;
    //序列化器
    private final CommonSerializer serializer;
    
    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }
    
    public SocketServer(String host, int port , Integer serializer) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry = new NacosServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanServices();
    }
    
    /**
     * 等待到一个请求就开启一个工作线程处理
     */
    @Override
    public void start(){
        
        try(ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.bind(new InetSocketAddress(host, port));
            logger.info("服务器正在启动...");
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket ;
            while((socket = serverSocket.accept()) != null){
                logger.info("客户端连接!\tIP为"+socket.getInetAddress() + ":" + socket.getPort());
                threadPool.execute(new SocketRequestHandlerThread(socket , serializer));
            }
        } catch (IOException e) {
            logger.error("连接时有错误发生:" , e);
        }
    }
}

package org.chw.rpc.socket.server;

import org.chw.rpc.RequestHandler;
import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.entity.RpcResponse;
import org.chw.rpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * 服务端接收到请求后的处理任务
 *
 * @Author CHW
 * @Date 2023/4/18
 **/
public class RequestHandlerThread implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);
    
    private Socket socket;
    private RequestHandler requestHandler;
    private ServiceRegistry serviceRegistry;
    
    public RequestHandlerThread(Socket socket, ServiceRegistry serviceRegistry) {
        this.socket = socket;
        this.requestHandler = new RequestHandler();
        this.serviceRegistry = serviceRegistry;
    }
    
    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            String interfaceName = rpcRequest.getInterfaceName();
            //通过接口名获取到服务
            Object service = serviceRegistry.getService(interfaceName);
            //执行该服务方法
            Object result = requestHandler.handle(rpcRequest, service);
            objectOutputStream.writeObject(RpcResponse.success(result));
            objectOutputStream.flush();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
    }
    
}

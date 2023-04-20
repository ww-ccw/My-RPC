package org.chw.rpc.socket.server;

import org.chw.rpc.RequestHandler;
import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.entity.RpcResponse;
import org.chw.rpc.registry.ServiceRegistry;
import org.chw.rpc.serializer.CommonSerializer;
import org.chw.rpc.socket.util.ObjectReader;
import org.chw.rpc.socket.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
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
    private CommonSerializer serializer;
    
    public RequestHandlerThread(Socket socket, ServiceRegistry serviceRegistry , CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = new RequestHandler();
        this.serviceRegistry = serviceRegistry;
        this.serializer = serializer;
    }
    
    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            String interfaceName = rpcRequest.getInterfaceName();
            Object service = serviceRegistry.getService(interfaceName);
            Object result = requestHandler.handle(rpcRequest, service);
            RpcResponse<Object> response = RpcResponse.success(result);
            ObjectWriter.writeObject(outputStream, response, serializer);
        } catch (IOException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
    }
    
}

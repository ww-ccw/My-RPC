package org.chw.rpc.transport.socket.server;

import org.chw.rpc.handler.RequestHandler;
import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.entity.RpcResponse;
import org.chw.rpc.provider.ServiceProvider;
import org.chw.rpc.serializer.CommonSerializer;
import org.chw.rpc.transport.socket.util.ObjectReader;
import org.chw.rpc.transport.socket.util.ObjectWriter;
import org.chw.rpc.util.SingletonFactory;
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
public class SocketRequestHandlerThread implements Runnable {
    
    private static final Logger logger = LoggerFactory.getLogger(SocketRequestHandlerThread.class);
    
    private Socket socket;
    private RequestHandler requestHandler;
    private CommonSerializer serializer;
    
    public SocketRequestHandlerThread(Socket socket , CommonSerializer serializer) {
        this.socket = socket;
        requestHandler = SingletonFactory.getInstance(RequestHandler.class);
        this.serializer = serializer;
    }
    
    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            Object result = requestHandler.handle(rpcRequest);
            RpcResponse<Object> response = RpcResponse.success(result , rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream, response, serializer);
        } catch (IOException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
    }
    
}

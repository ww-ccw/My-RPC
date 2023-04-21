package org.chw.rpc.transport.socket.client;

import org.chw.rpc.transport.RpcClient;
import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.entity.RpcResponse;
import org.chw.rpc.enumeration.ResponseCode;
import org.chw.rpc.enumeration.RpcError;
import org.chw.rpc.exception.RpcException;
import org.chw.rpc.serializer.CommonSerializer;
import org.chw.rpc.transport.socket.util.ObjectReader;
import org.chw.rpc.transport.socket.util.ObjectWriter;
import org.chw.rpc.util.RpcMessageChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * 远程方法调用的消费者（客户端）
 *
 * @Author CHW
 * @Date 2023/4/17
 **/
public class SocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);
    
    private String host;
    private int port;
    private CommonSerializer serializer;
    
    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
    
    /**
     * 向消费者发送请求
     */
    public Object sendRequest(RpcRequest rpcRequest ){
    
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        
        try(Socket socket = new Socket(host , port)){
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            Object obj = ObjectReader.readObject(inputStream);
            RpcResponse rpcResponse = (RpcResponse) obj;
            if(rpcResponse == null){
                logger.error("服务调用失败,service:{}" ,rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE , "service:" + rpcRequest.getInterfaceName());
            }
            if (rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode()){
                logger.error("调用服务失败,service:{} , response:{}" , rpcRequest.getInterfaceName() , rpcResponse );
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE , "service:"+rpcRequest.getInterfaceName());
            }
            RpcMessageChecker.check(rpcRequest, rpcResponse);
            return rpcResponse.getData();
        } catch (IOException e) {
            logger.error("调用远程方法时有错误发生" , e);
            throw new RpcException("服务调用失败:" , e);
        }
    }
}

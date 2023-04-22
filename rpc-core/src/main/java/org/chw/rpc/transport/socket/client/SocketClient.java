package org.chw.rpc.transport.socket.client;

import org.chw.rpc.registry.NacosServiceDiscovery;
import org.chw.rpc.registry.ServiceDiscovery;
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
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 远程方法调用的消费者（客户端）
 *
 * @Author CHW
 * @Date 2023/4/17
 **/
public class SocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);
    
    private final ServiceDiscovery serviceDiscovery;
    
    private final CommonSerializer serializer;
    
    public SocketClient() {
        this(DEFAULT_SERIALIZER);
    }
    
    public SocketClient(Integer serializer) {
        this.serviceDiscovery = new NacosServiceDiscovery();
        this.serializer = CommonSerializer.getByCode(serializer);
    }
    
    
    /**
     * 向消费者发送请求
     */
    public Object sendRequest(RpcRequest rpcRequest ){
        
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
    
        try(Socket socket = new Socket()){
            socket.connect(inetSocketAddress);
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

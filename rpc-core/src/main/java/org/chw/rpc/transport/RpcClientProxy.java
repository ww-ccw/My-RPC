package org.chw.rpc.transport;


import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.entity.RpcResponse;
import org.chw.rpc.transport.netty.client.NettyClient;
import org.chw.rpc.transport.socket.client.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Author chw
 * RPC客户端动态代理
 */
public class RpcClientProxy implements InvocationHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(RpcClientProxy.class);
    
    private final RpcClient client;
    
    public RpcClientProxy(RpcClient client) {
        this.client = client;
    }
    
    /**
     * 动态代理生成实例对象
     * @param clazz 要实现的接口
     * @param <T> 代理对象的类型
     * @return 代理获得的实例转换成T类型返回
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz){
        return (T)Proxy.newProxyInstance(clazz.getClassLoader() , new Class<?>[]{clazz} , this);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args){
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
        
        RpcRequest rpcRequest = new RpcRequest( UUID.randomUUID().toString() , method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes());
        Object result = null;
        if (client instanceof NettyClient) {
            CompletableFuture<RpcResponse> completableFuture = (CompletableFuture<RpcResponse>) client.sendRequest(rpcRequest);
            try {
                result = completableFuture.get().getData();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("方法调用请求发送失败", e);
                return null;
            }
        }
        if (client instanceof SocketClient) {
            RpcResponse rpcResponse = (RpcResponse) client.sendRequest(rpcRequest);
            result = rpcResponse.getData();
        }
        return result;
    }
}

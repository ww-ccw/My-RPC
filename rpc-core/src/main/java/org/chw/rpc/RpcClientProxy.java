package org.chw.rpc;


import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.transport.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

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
    
    /**
     * 动态代理的实现，当消费者调用代理对象的方法时就会执行这个方法
     * 通过代理对象，获得到将要调用的方法所属类名、方法名、方法参数类型、方法参数，
     * 通过这四个参数构建 RPCRequest对象，并使用该对象发起请求得到返回结果
     * @return 服务提供者返回的data
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args){
        logger.info("调用方法: {}#{}", method.getDeclaringClass().getName(), method.getName());
    
        RpcRequest rpcRequest = new RpcRequest( UUID.randomUUID().toString() , method.getDeclaringClass().getName(),
                method.getName(), args, method.getParameterTypes());
        //发起请求返回结果
        return client.sendRequest(rpcRequest);
    }
}

package org.chw.rpc.transport;

import org.chw.rpc.serializer.CommonSerializer;

/**
 * 服务端接口，服务端的实现可以有不同的方式，基于Socket、Netty等
 *
 * @Author CHW
 * @Date 2023/4/19
 **/
public interface RpcServer {
    /**
     * 默认序列化器
     */
    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;
    
    void start();
    
    /**
     * 向nacos注册服务
     * @param service
     * @param serviceClass
     * @param <T>
     */
    <T> void publishService(T service, Class<T> serviceClass);
    
}

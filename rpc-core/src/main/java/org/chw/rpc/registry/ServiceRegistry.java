package org.chw.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册中心通用接口
 *
 * @Author CHW
 * @Date 2023/4/18
 **/
public interface ServiceRegistry {
    
    /**
     * 将服务注册进注册注册中心
     * @param serviceName 服务名称
     * @param inetSocketAddress 提供服务的地址
     */
    <T> void register(String serviceName, InetSocketAddress inetSocketAddress);
    
    /**
     * 根据服务名称查找服务实体
     */
    InetSocketAddress lookupService(String serviceName);
}

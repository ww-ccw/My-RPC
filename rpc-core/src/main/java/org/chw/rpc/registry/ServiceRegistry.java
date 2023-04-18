package org.chw.rpc.registry;

/**
 * 通用服务注册接口
 *
 * @Author CHW
 * @Date 2023/4/18
 **/
public interface ServiceRegistry {
    /**
     * 将服务注册进注册表
     * @param service 待注册的服务实体
     */
    <T> void registry(T service);
    
    /**
     * 根据服务名称获取服务实体
     */
    Object getService(String serviceName);
}

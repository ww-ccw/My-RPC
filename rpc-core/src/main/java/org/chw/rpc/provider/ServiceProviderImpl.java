package org.chw.rpc.provider;

import org.chw.rpc.enumeration.RpcError;
import org.chw.rpc.exception.RpcException;
import org.chw.rpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认服务注册表，保存服务端本地服务
 *
 * @Author CHW
 * @Date 2023/4/18
 **/
public class ServiceProviderImpl implements ServiceProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);
    
    /**
     * serviceMap中存了(接口规范名,服务实现对象)
     */
    private static final Map<String , Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();
    
    @Override
    public <T> void addServiceProvider(T service , Class<T> serviceClass) {
        String serviceName = serviceClass.getCanonicalName();
    
        if (registeredService.contains(serviceName)) return;
        //将服务对象注册到map中
        registeredService.add(serviceName);
        serviceMap.put(serviceName, service);
        logger.info("向接口: {} 注册服务: {}", service.getClass().getInterfaces(), serviceName);
        
    }
    
    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if (service == null){
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}

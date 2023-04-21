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
    public synchronized  <T> void addServiceProvider(T service) {
        String serviceName = service.getClass().getCanonicalName();
        if (registeredService.contains(serviceName)) return;
        //得到该服务对象的类实现的所有接口
        Class<?>[] interfaces = service.getClass().getInterfaces();
        if (interfaces.length == 0 ){
            throw new RpcException(RpcError.SERVICE_NOT_IMPLEMENT_ANY_INTERFACE);
        }
        //将服务对象注册到map中
        registeredService.add(serviceName);
        for(Class i : interfaces){
            if (serviceMap.containsKey(i.getCanonicalName())){
                logger.warn("服务冲突:{}和{}都有实现{},目前该服务选用了{}为默认实现" , serviceName , serviceMap.get(i.getCanonicalName()).getClass().getCanonicalName() , i.getCanonicalName() , serviceName);
            }
            serviceMap.put(i.getCanonicalName() , service);
        }
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

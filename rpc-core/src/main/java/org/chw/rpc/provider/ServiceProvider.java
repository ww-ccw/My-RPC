package org.chw.rpc.provider;

/**
 * 本地服务保存接口
 *
 * @Author CHW
 * @Date 2023/4/21
 **/
public interface ServiceProvider {
    <T> void addServiceProvider(T service);
    
    Object getServiceProvider(String serviceName);
}

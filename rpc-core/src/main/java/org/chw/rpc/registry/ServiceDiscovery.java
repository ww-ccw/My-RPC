package org.chw.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 *
 * @Author CHW
 * @Date 2023/4/21
 **/
public interface ServiceDiscovery {
    
    /**
     * 根据服务名称查找服务实体
     *
     * @param serviceName 服务名称
     * @return 服务实体
     */
    InetSocketAddress lookupService(String serviceName);
}

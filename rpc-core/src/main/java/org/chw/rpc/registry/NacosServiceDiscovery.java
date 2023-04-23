package org.chw.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.NonNull;
import org.chw.rpc.enumeration.RpcError;
import org.chw.rpc.exception.RpcException;
import org.chw.rpc.loadbalancer.LoadBalancer;
import org.chw.rpc.loadbalancer.RoundRobinLoadBalancer;
import org.chw.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 发现服务
 *
 * @Author CHW
 * @Date 2023/4/21
 **/
public class NacosServiceDiscovery implements ServiceDiscovery{
    
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);
    
    private final LoadBalancer loadBalancer;
    
    public NacosServiceDiscovery(){
        this(new RoundRobinLoadBalancer());
    }
    
    public NacosServiceDiscovery(@NonNull LoadBalancer loadBalancer) {
         this.loadBalancer = loadBalancer;
    }
    
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try{
            List<Instance> instances = NacosUtil.getAllInstance(serviceName);
            if (instances.size() == 0){
                logger.error("找不到对应服务"+serviceName);
                throw new RpcException(RpcError.SERVICE_NOT_FOUND);
            }
            Instance instance = loadBalancer.select(instances);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return null;
    }
}

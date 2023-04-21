package org.chw.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.chw.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author CHW
 * @Date 2023/4/21
 **/
public class NacosServiceDiscovery implements ServiceDiscovery{
    
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceDiscovery.class);
    
    private final NamingService namingService;
    
    public NacosServiceDiscovery() {
        namingService = NacosUtil.getNacosNamingService();
    }
    
    @Override
    public InetSocketAddress lookupService(String serviceName) {
        try{
            List<Instance> instances = NacosUtil.getAllInstance(namingService , serviceName);
            Instance instance = instances.get(0);
            return new InetSocketAddress(instance.getIp(), instance.getPort());
        } catch (NacosException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package org.chw.rpc.registry;

import com.alibaba.nacos.api.exception.NacosException;
import org.chw.rpc.enumeration.RpcError;
import org.chw.rpc.exception.RpcException;
import org.chw.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @Author CHW
 * @Date 2023/4/21
 **/
public class NacosServiceRegistry implements ServiceRegistry{
    
    private static final Logger logger = LoggerFactory.getLogger(NacosServiceRegistry.class);
    
    @Override
    public <T> void register(String serviceName, InetSocketAddress inetSocketAddress) {
        try {
            NacosUtil.registerService(serviceName, inetSocketAddress);
        } catch (NacosException e) {
            logger.error("注册服务失败:" , e);
            throw new RpcException(RpcError.REGISTER_SERVICE_FAILED);
        }
    }
    
    
}

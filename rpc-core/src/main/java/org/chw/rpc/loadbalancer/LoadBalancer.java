package org.chw.rpc.loadbalancer;

import com.alibaba.nacos.api.naming.pojo.Instance;

import java.util.List;

/**
 * @Author CHW
 * @Date 2023/4/23
 **/
public interface LoadBalancer {
    
    Instance select(List<Instance> instances);
}

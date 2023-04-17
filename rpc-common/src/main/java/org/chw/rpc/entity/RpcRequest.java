package org.chw.rpc.entity;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * Author CHW
 * 消费者向服务者发生的请求对象,传输格式
 */
@Data
@Builder
public class RpcRequest implements Serializable {
    
    /**
     * 待调用接口名称
     */
    private String interfaceName;
    
    /**
     * 待调用方法名称
     */
    private String methodName;
    
    /**
     * 待调用方法的参数
     */
    private Object[] parameters;
    
    /**
     * 待调用方法的参数类型
     */
    private Class<?>[] paramTypes;
    
}

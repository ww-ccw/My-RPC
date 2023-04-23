package org.chw.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Author CHW
 * 消费者向服务者发生的请求对象,传输格式
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RpcRequest implements Serializable {
    
    /**
     * 请求号
     */
    private String requestId;

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
    
    /**
     * 是否是心跳包
     */
    private Boolean heartBeat;
    
}

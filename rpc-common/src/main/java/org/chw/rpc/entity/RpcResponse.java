package org.chw.rpc.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.chw.rpc.enumeration.ResponseCode;

import java.io.Serializable;

/**
 * Author CHW
 * 服务提供者处理完消费者的请求或者出错了，返回的对象
 */
@Data
@NoArgsConstructor
public class RpcResponse<T> implements Serializable {
    
    /**
     * 响应状态码
     */
    private Integer statusCode;
    
    /**
     * 响应状态补充信息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;

    public static <T> RpcResponse<T> success(T data){
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(ResponseCode.SUCCESS.getCode());
        response.setMessage(ResponseCode.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }
    
    public static <T> RpcResponse<T> fail(T data){
        RpcResponse<T> response = new RpcResponse<>();
        response.setStatusCode(ResponseCode.FAIL.getCode());
        response.setMessage(ResponseCode.FAIL.getMessage());
        response.setData(data);
        return response;
    }
    
    
}

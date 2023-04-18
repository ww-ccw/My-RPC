package org.chw.rpc.exception;

import org.chw.rpc.enumeration.RpcError;

/**
 * @Author CHW
 * @Date 2023/4/18
 **/
public class RpcException extends RuntimeException{
    
    public  RpcException(RpcError error , String detail){
        super(error.getMessage()+":\t" +detail);
    }
    
    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public RpcException(RpcError error) {
        super(error.getMessage());
    }
    
    
}

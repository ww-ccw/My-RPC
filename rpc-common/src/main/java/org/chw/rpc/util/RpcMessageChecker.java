package org.chw.rpc.util;

import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.entity.RpcResponse;
import org.chw.rpc.enumeration.ResponseCode;
import org.chw.rpc.enumeration.RpcError;
import org.chw.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author CHW
 * @Date 2023/4/20
 **/
public class RpcMessageChecker {
    
    private static final Logger logger = LoggerFactory.getLogger(RpcMessageChecker.class);
    
    private RpcMessageChecker() {
    }
    
    public static void check(RpcRequest rpcRequest, RpcResponse rpcResponse) {
        if (rpcResponse == null) {
            logger.error("调用服务失败,serviceName:{}", rpcRequest.getInterfaceName());
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, "interfaceName" + ":" + rpcRequest.getInterfaceName());
        }
        
        if (!rpcRequest.getRequestId().equals(rpcResponse.getRequestId())) {
            throw new RpcException(RpcError.RESPONSE_NOT_MATCH, "interfaceName" + ":" + rpcRequest.getInterfaceName());
        }
        
        if (rpcResponse.getStatusCode() == null || !rpcResponse.getStatusCode().equals(ResponseCode.SUCCESS.getCode())) {
            logger.error("调用服务失败,serviceName:{},RpcResponse:{}", rpcRequest.getInterfaceName(), rpcResponse);
            throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, "interfaceName" + ":" + rpcRequest.getInterfaceName());
        }
    }
    
}

package org.chw.rpc;

import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.entity.RpcResponse;
import org.chw.rpc.enumeration.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 实际进行过程调用的工作线程
 * @Author CHW
 * @Date 2023/4/17
 **/
public class RequestHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    
    public  Object handle(RpcRequest rpcRequest, Object service) {
        Object result = null;
        try {
            result = invokeTargetMethod(rpcRequest, service);
            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error("调用或发送时有错误发生：", e);
        } return result;
    }
    
    private  Object invokeTargetMethod(RpcRequest rpcRequest, Object service) throws IllegalAccessException, InvocationTargetException {
        Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND , rpcRequest.getRequestId());
        }
        return method.invoke(service, rpcRequest.getParameters());
    }
    
}

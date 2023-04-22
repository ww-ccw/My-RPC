package org.chw.rpc.handler;

import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.entity.RpcResponse;
import org.chw.rpc.enumeration.ResponseCode;
import org.chw.rpc.provider.ServiceProvider;
import org.chw.rpc.provider.ServiceProviderImpl;
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
    private static final ServiceProvider serviceProvider;
    
    static {
        serviceProvider = new ServiceProviderImpl();
    }
    
    public  Object handle(RpcRequest rpcRequest) {
        
            Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
            return invokeTargetMethod(rpcRequest, service);
        
    }
    
    private  Object invokeTargetMethod(RpcRequest rpcRequest, Object service){
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND , rpcRequest.getRequestId());
        }
        return result;
    }
    
}

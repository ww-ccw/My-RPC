package org.chw.rpc.server;

import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.entity.RpcResponse;
import org.chw.rpc.enumeration.ResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * @Author CHW
 * @Date 2023/4/17
 * 实际进行过程调用的工作线程
 **/
public class RequestHandler implements Runnable{
    
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    
    private Socket socket;
    private Object service;
    
    public RequestHandler(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }
    
    /**
     * 工作线程，通过流获取到客户端传来的RpcRequest，解析请求，反射执行对应方法
     */
    @Override
    public void run() {
        try(ObjectInputStream objectInputStream =  new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream()))
        {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
           
            //执行方法
            Object returnObject = invokeMethod(rpcRequest);
            //封装返回
            RpcResponse<Object> result = RpcResponse.success(returnObject);
            objectOutputStream.writeObject(result);
            objectOutputStream.flush();
            
        } catch (IOException | ClassNotFoundException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    
    }
    
    /**
     * 反射执行
     * @param rpcRequest 请求
     * @return 返回结果
     */
    private Object invokeMethod(RpcRequest rpcRequest) throws IllegalAccessException, ClassNotFoundException, InvocationTargetException {
        Class<?> clazz = Class.forName(rpcRequest.getInterfaceName());
        if (!clazz.isAssignableFrom(service.getClass())){
            return RpcResponse.fail(ResponseCode.CLASS_NOT_FOUND);
        }
        Method method = null;
        try{
            method = service.getClass().getMethod(rpcRequest.getMethodName() , rpcRequest.getParamTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.METHOD_NOT_FOUND);
        }
        return method.invoke(service , rpcRequest.getParameters());
    }
}

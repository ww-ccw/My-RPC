package org.chw.rpc.server;

import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.entity.RpcResponse;
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
public class WorkerThread implements Runnable{
    
    private static final Logger logger = LoggerFactory.getLogger(WorkerThread.class);
    
    private Socket socket;
    private Object service;
    
    public WorkerThread(Socket socket, Object service) {
        this.socket = socket;
        this.service = service;
    }
    
    /**
     * 监听Socket，通过流获取到客户端传来的RpcRequest，解析请求，反射执行对应方法
     */
    @Override
    public void run() {
        try(ObjectInputStream objectInputStream =  new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream()))
        {
            RpcRequest rpcRequest = (RpcRequest) objectInputStream.readObject();
            Method method = service.getClass().getMethod(rpcRequest.getMethodName() , rpcRequest.getParamTypes());
            
            Object returnObject = method.invoke(service , rpcRequest.getParameters());
            //封装返回
            RpcResponse<Object> result = RpcResponse.success(returnObject);
            objectOutputStream.writeObject(result);
            objectOutputStream.flush();
            
        } catch (IOException | ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    
    }
}

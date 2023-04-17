package org.chw.rpc.client;

import org.chw.rpc.entity.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @Author CHW
 * @Date 2023/4/17
 *
 * 远程方法调用的消费者（客户端）
 **/
public class RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(RpcClient.class);
    
    /**
     * 向消费者发送请求
     */
    public Object sendRequest(RpcRequest rpcRequest , String host , int port){
        try(Socket socket = new Socket(host , port)){
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(rpcRequest);
            objectOutputStream.flush();
            return objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error("调用远程方法时有错误发生" , e);
            return null;
        }
    }
}

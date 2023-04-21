package org.chw.test;

import org.chw.rpc.transport.RpcServer;
import org.chw.rpc.api.HelloService;
import org.chw.rpc.serializer.HessianSerializer;
import org.chw.rpc.transport.socket.server.SocketServer;


/**
 * @Author CHW
 * @Date 2023/4/17
 * 服务提供方（服务端）
 **/
public class SocketTestServer {
    
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl2();
        RpcServer rpcServer = new SocketServer("localhost" , 9999);
        rpcServer.setSerializer(new HessianSerializer());
        rpcServer.publishService(helloService, HelloService.class);
        rpcServer.start( );
    }
}

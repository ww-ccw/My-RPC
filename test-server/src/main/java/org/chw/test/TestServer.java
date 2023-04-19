package org.chw.test;

import org.chw.rpc.RpcServer;
import org.chw.rpc.api.HelloService;
import org.chw.rpc.registry.DefaultServiceRegistry;
import org.chw.rpc.registry.ServiceRegistry;
import org.chw.rpc.socket.server.SocketServer;


/**
 * @Author CHW
 * @Date 2023/4/17
 * 服务提供方（服务端）
 **/
public class TestServer {
    
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry serviceRegistry = new DefaultServiceRegistry();
        serviceRegistry.register(helloService);
        RpcServer rpcServer = new SocketServer(serviceRegistry);
        rpcServer.start( 9000);
    }
}

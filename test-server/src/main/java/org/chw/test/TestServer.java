package org.chw.test;

import org.chw.rpc.api.HelloService;
import org.chw.rpc.server.RpcServer;

/**
 * @Author CHW
 * @Date 2023/4/17
 * 服务提供方（服务端）
 **/
public class TestServer {
    
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new RpcServer();
        rpcServer.register(helloService , 9000);
    }
}

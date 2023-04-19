package org.chw.test;

import org.chw.rpc.api.HelloService;
import org.chw.rpc.netty.server.NettyServer;
import org.chw.rpc.registry.DefaultServiceRegistry;
import org.chw.rpc.registry.ServiceRegistry;

/**
 * @Author CHW
 * @Date 2023/4/19
 **/
public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        ServiceRegistry registry = new DefaultServiceRegistry();
        registry.register(helloService);
        NettyServer server = new NettyServer();
        server.start(9999);
    }
}

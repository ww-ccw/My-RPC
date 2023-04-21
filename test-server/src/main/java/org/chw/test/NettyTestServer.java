package org.chw.test;

import org.chw.rpc.api.HelloService;
import org.chw.rpc.serializer.KryoSerializer;
import org.chw.rpc.transport.RpcServer;
import org.chw.rpc.transport.netty.server.NettyServer;

/**
 * @Author CHW
 * @Date 2023/4/19
 **/
public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        RpcServer rpcServer = new NettyServer("localhost" , 9998);
        rpcServer.setSerializer(new KryoSerializer());
        rpcServer.publishService(helloService,HelloService.class);
        rpcServer.start();
    }
}

package org.chw.tst;

import org.chw.rpc.transport.RpcClient;

import org.chw.rpc.transport.RpcClientProxy;
import org.chw.rpc.api.HelloObject;
import org.chw.rpc.api.HelloService;

import org.chw.rpc.serializer.HessianSerializer;
import org.chw.rpc.transport.netty.client.NettyClient;

/**
 * @Author CHW
 * @Date 2023/4/19
 **/
public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient();
        client.setSerializer(new HessianSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}

package org.chw.tst;

import org.chw.rpc.RpcClient;
import org.chw.rpc.RpcClientProxy;
import org.chw.rpc.api.HelloObject;
import org.chw.rpc.api.HelloService;
import org.chw.rpc.netty.client.NettyClient;
import org.chw.rpc.serializer.HessianSerializer;

/**
 * @Author CHW
 * @Date 2023/4/19
 **/
public class NettyTestClient {
    public static void main(String[] args) {
        RpcClient client = new NettyClient("127.0.0.1", 9999);
        client.setSerializer(new HessianSerializer());
        RpcClientProxy rpcClientProxy = new RpcClientProxy(client);
        HelloService helloService = rpcClientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}

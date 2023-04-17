package org.chw.tst;

import org.chw.rpc.api.HelloObject;
import org.chw.rpc.api.HelloService;
import org.chw.rpc.client.RpcClientProxy;

/**
 * @Author CHW
 * @Date 2023/4/17
 * 消费者（客户端）
 **/
public class TestClient {
    public static void main(String[] args) {
        RpcClientProxy proxy = new RpcClientProxy("localhost" , 9000);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12 , "This is a message");
        String res = helloService.Hello(object);
        System.out.println(res);
    }
}

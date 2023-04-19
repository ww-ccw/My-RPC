package org.chw.tst;

import org.chw.rpc.api.HelloObject;
import org.chw.rpc.api.HelloService;
import org.chw.rpc.RpcClientProxy;
import org.chw.rpc.socket.client.SocketClient;

/**
 * @Author CHW
 * @Date 2023/4/17
 * 消费者（客户端）
 **/
public class TestClient {
    public static void main(String[] args) {
        SocketClient client = new SocketClient("127.0.0.1", 9000);
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12 , "This is a message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}

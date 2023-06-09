package org.chw.test;

import org.chw.rpc.annotation.ServiceScan;
import org.chw.rpc.transport.RpcServer;
import org.chw.rpc.transport.socket.server.SocketServer;


/**
 * @Author CHW
 * @Date 2023/4/17
 * 服务提供方（服务端）
 **/
@ServiceScan
public class SocketTestServer {
    
    public static void main(String[] args) {
        RpcServer server = new SocketServer("127.0.0.1", 9998);
        server.start();
    }
}

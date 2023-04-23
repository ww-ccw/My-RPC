package org.chw.test;

import org.chw.rpc.annotation.ServiceScan;
import org.chw.rpc.transport.RpcServer;
import org.chw.rpc.transport.netty.server.NettyServer;

/**
 * @Author CHW
 * @Date 2023/4/19
 **/
@ServiceScan
public class NettyTestServer {
    public static void main(String[] args) {
    
        RpcServer server = new NettyServer("127.0.0.1", 9999);
        server.start();
    }
}

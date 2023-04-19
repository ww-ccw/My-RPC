package org.chw.rpc;

/**
 * 服务端接口，服务端的实现可以有不同的方式，基于Socket、Netty等
 *
 * @Author CHW
 * @Date 2023/4/19
 **/
public interface RpcServer {
    void start(int port);
}

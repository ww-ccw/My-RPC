package org.chw.rpc;

import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.serializer.CommonSerializer;

/**
 * 客户端接口，客户端的实现可以有不同的方式，基于Socket、Netty等
 *
 * @Author CHW
 * @Date 2023/4/19
 **/
public interface RpcClient {
    Object sendRequest(RpcRequest rpcRequest);
    
    void setSerializer(CommonSerializer serializer);

    
}

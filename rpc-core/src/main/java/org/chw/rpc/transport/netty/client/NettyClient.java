package org.chw.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.chw.rpc.registry.NacosServiceDiscovery;
import org.chw.rpc.registry.ServiceDiscovery;
import org.chw.rpc.transport.RpcClient;
import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.entity.RpcResponse;
import org.chw.rpc.serializer.CommonSerializer;
import org.chw.rpc.util.SingletonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;


/**
 * 使用Netty实现的NIO方式的客户端类
 * @Author CHW
 * @Date 2023/4/19
 **/
public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    
    private static final EventLoopGroup group;
    /**
     * 用于配置和启动Netty客户端的各种参数，例如线程、通道类型、处理器等。一旦配置好了，就可以调用Bootstrap.connect()方法连接到服务器。
     */
    private static final Bootstrap bootstrap;
    
    static {
        bootstrap = new Bootstrap();
        //EventLoopGroup是Netty的一个线程组，内部维护一组NIO线程，由这些线程负责连接、读写等网络操作
        group = new NioEventLoopGroup();
        bootstrap.group(group)
                //指定连接类型为NIO
                .channel(NioSocketChannel.class);
    }
    
    private final CommonSerializer serializer;
    private final ServiceDiscovery serviceDiscovery;
    
    private final UnprocessedRequests unprocessedRequests;
    
    public NettyClient() {
        this(DEFAULT_SERIALIZER);
    }
    
    public NettyClient(Integer serializer) {
        this.serviceDiscovery = new NacosServiceDiscovery();
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    
    }
    
    

    
    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
    
        CompletableFuture<RpcResponse> resultFuture = new CompletableFuture<>();
        try {
            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName());
            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
            if (!channel.isActive()) {
                group.shutdownGracefully();
                return null;
            }
            //将请求加入未完成名单
            unprocessedRequests.put(rpcRequest.getRequestId(), resultFuture);
            //发送请求
            channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
                if (future1.isSuccess()) {
                    logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
                } else {
                    //请求失败关闭当前这个连接
                    future1.channel().close();
                    //抛出异常，标记这个任务已经完成
                    resultFuture.completeExceptionally(future1.cause());
                    logger.error("发送消息时有错误发生: ", future1.cause());
                }
            });
        } catch (InterruptedException e) {
            //将请求移出未完成名单
            unprocessedRequests.remove(rpcRequest.getRequestId());
            logger.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
        return resultFuture;
    }
}

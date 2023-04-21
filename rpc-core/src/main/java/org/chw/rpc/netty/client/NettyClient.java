package org.chw.rpc.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.chw.rpc.RpcClient;
import org.chw.rpc.codec.CommonDecoder;
import org.chw.rpc.codec.CommonEncoder;
import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.entity.RpcResponse;
import org.chw.rpc.enumeration.RpcError;
import org.chw.rpc.exception.RpcException;
import org.chw.rpc.serializer.CommonSerializer;
import org.chw.rpc.serializer.HessianSerializer;
import org.chw.rpc.serializer.KryoSerializer;
import org.chw.rpc.util.RpcMessageChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 使用Netty实现的NIO方式的客户端类
 * @Author CHW
 * @Date 2023/4/19
 **/
public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    
    
    /**
     * 用于配置和启动Netty客户端的各种参数，例如线程、通道类型、处理器等。一旦配置好了，就可以调用Bootstrap.connect()方法连接到服务器。
     */
    private static final Bootstrap bootstrap;
    
    static {
        bootstrap = new Bootstrap();
        //EventLoopGroup是Netty的一个线程组，内部维护一组NIO线程，由这些线程负责连接、读写等网络操作
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                //指定连接类型为NIO
                .channel(NioSocketChannel.class)
                //设置SO_KEEPALIVE选项，保持连接状态。SO_KEEPALIVE会通过心跳来测试连接是否存在
                .option(ChannelOption.SO_KEEPALIVE , true);
    }
    
    private String host;
    private int port;
    private CommonSerializer serializer;
    
    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
    

    
    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
    
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
    
        //创建一个原子的返回类
        AtomicReference<Object> result = new AtomicReference<>(null);
        
        try{
            //获取channel
            Channel channel = ChannelProvider.get(new InetSocketAddress(host, port), serializer);
            //如果channel已经获得且处于活跃状态
            if(channel.isActive()) {
                //向channel写入rpcRequest对象并刷新，添加一个监听器来处理操作结果。如果future1操作成功，打印“客户端发送消息”信息；否则，打印相应的错误信息。
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if (future1.isSuccess()){
                        logger.info(String.format("客户端发送消息: %s" , rpcRequest.toString()));
                    }else {
                        logger.error("发送消息时有错误发生:" , future1.cause());
                    }
                });
                //关闭channel
                channel.closeFuture().sync();
                //创建一个属性键key，用于获取channel的RpcResponse属性
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
                RpcResponse rpcResponse = channel.attr(key).get();
                RpcMessageChecker.check(rpcRequest, rpcResponse);
                
                result.set(rpcResponse.getData());
            } else {
                System.exit(0);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result.get();
    }
}

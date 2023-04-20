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
import org.chw.rpc.serializer.HessianSerializer;
import org.chw.rpc.serializer.KryoSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用Netty实现的NIO方式的客户端类
 * @Author CHW
 * @Date 2023/4/19
 **/
public class NettyClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);
    
    private String host;
    private int port;
    /**
     * 用于配置和启动Netty客户端的各种参数，例如线程、通道类型、处理器等。一旦配置好了，就可以调用Bootstrap.connect()方法连接到服务器。
     */
    private static final Bootstrap bootstrap;
    
    public NettyClient(String host, int port) {
        this.host = host;
        this.port = port;
    }
    
    static {
        bootstrap = new Bootstrap();
        //EventLoopGroup是Netty的一个线程组，内部维护一组NIO线程，由这些线程负责连接、读写等网络操作
        EventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
                //指定连接类型为NIO
                .channel(NioSocketChannel.class)
                //设置SO_KEEPALIVE选项，保持连接状态。SO_KEEPALIVE会通过心跳来测试连接是否存在
                .option(ChannelOption.SO_KEEPALIVE , true)
                //设置客户端的ChannelHandler，用于处理IO操作。
                .handler(new ChannelInitializer<SocketChannel>() {
    
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        //ChannelPipeline添加的Handle必须是ChannelInboundHandler和ChannelOutboundHandler之一,一个是处理入站数据，一个处理出站数据。按添加顺序处理
                        pipeline.addLast(new CommonDecoder())
                                .addLast(new CommonEncoder(new HessianSerializer()))
                                .addLast(new NettyClientHandler());
                    }
                });
    }
    

    
    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        try{
            //创建一个ChannelFuture实例future，表示异步的I/O操作的结果，sync会阻塞当前线程，直到连接成功或超时
            ChannelFuture future = bootstrap.connect(host , port).sync();
            logger.info("客户端连接到服务器{}:{}" , host , port);
            //获取连接成功的Channel实例
            Channel channel = future.channel();
            if (channel != null){
                //向channel写入rpcRequest对象并刷新，添加一个监听器来处理操作结果。如果future1操作成功，打印“客户端发送消息”信息；否则，打印相应的错误信息。
                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
                    if (future1.isSuccess()){
                        logger.info(String.format("客户端发送消息: %s" , rpcRequest.toString()));
                    }else {
                        logger.error("发送消息时有错误发生:" , future.cause());
                    }
                });
                //关闭channel
                channel.closeFuture().sync();
                //创建一个属性键key，用于获取channel的RpcResponse属性
                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
                //从channel的属性中获取RpcResponse对象
                RpcResponse rpcResponse = channel.attr(key).get();
                //返回RpcResponse对象的data属性值。
                return rpcResponse.getData();
            }
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}

package org.chw.rpc.transport.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.chw.rpc.transport.RpcServer;
import org.chw.rpc.codec.CommonDecoder;
import org.chw.rpc.codec.CommonEncoder;
import org.chw.rpc.enumeration.RpcError;
import org.chw.rpc.exception.RpcException;
import org.chw.rpc.provider.ServiceProvider;
import org.chw.rpc.provider.ServiceProviderImpl;
import org.chw.rpc.registry.NacosServiceRegistry;
import org.chw.rpc.registry.ServiceRegistry;
import org.chw.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 使用Netty NIO的方式的服务端
 *
 * @Author CHW
 * @Date 2023/4/19
 **/
public class NettyServer implements RpcServer {
    
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    
    private final String host;
    private final int port;

    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;
    
    private CommonSerializer serializer;
    
    public NettyServer(String host, int port) {
        this.host = host;
        this.port = port;
        serviceRegistry = new NacosServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
    }
    
    @Override
    public void setSerializer(CommonSerializer serializer) {
        this.serializer = serializer;
    }
    
    @Override
    public <T> void publishService(Object service, Class<T> serviceClass) {
        if (serializer == null){
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceRegistry.register(serviceClass.getCanonicalName() , new InetSocketAddress(host , port));
        serviceProvider.addServiceProvider(service);
    }
    
    
    @Override
    public void start() {
    
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 256)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new CommonEncoder(serializer));
                            pipeline.addLast(new CommonDecoder());
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            ChannelFuture future = serverBootstrap.bind(host , port).sync();
            future.channel().closeFuture().sync();
            
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    
}

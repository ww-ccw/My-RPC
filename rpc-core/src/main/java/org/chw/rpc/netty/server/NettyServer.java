package org.chw.rpc.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.chw.rpc.RpcServer;
import org.chw.rpc.codec.CommonDecoder;
import org.chw.rpc.codec.CommonEncoder;
import org.chw.rpc.serializer.HessianSerializer;
import org.chw.rpc.serializer.JsonSerializer;
import org.chw.rpc.serializer.KryoSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 使用Netty NIO的方式的服务端
 *
 * @Author CHW
 * @Date 2023/4/19
 **/
public class NettyServer implements RpcServer {
    
    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    
    @Override
    public void start(int port) {
        // 创建线程组，用于处理连接请求
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 创建线程组，用于处理已建立连接的 IO 读写操作
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 创建服务器启动引导类实例
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 设置 boss 线程组和 worker 线程组
            serverBootstrap.group(bossGroup, workerGroup)
                    // 指定使用 NIO 传输 Channel
                    .channel(NioServerSocketChannel.class)
                    // 添加日志处理器，输出 INFO 日志级别的日志信息
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 设置最大连接数为 256
                    .option(ChannelOption.SO_BACKLOG, 256)
                    // 开启 TCP 底层心跳机制，保证连接活跃
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    // 关闭 Nagle 算法，降低延迟
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // 初始化子 Channel 的处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            // 添加序列化编码器，将对象转化为字节数组进行网络传输
                            pipeline.addLast(new CommonEncoder(new HessianSerializer()));
                            // 添加序列化解码器，将字节数组转化为对象
                            pipeline.addLast(new CommonDecoder());
                            // 添加业务处理器，用于处理具体的业务逻辑
                            pipeline.addLast(new NettyServerHandler());
                        }
                    });
            // 绑定端口，并同步等待绑定结果
            ChannelFuture future = serverBootstrap.bind(port).sync();
            // 关闭 Channel，并同步等待关闭结果
            future.channel().closeFuture().sync();
            
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
    
}

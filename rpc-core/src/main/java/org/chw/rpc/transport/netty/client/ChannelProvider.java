package org.chw.rpc.transport.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.chw.rpc.codec.CommonDecoder;
import org.chw.rpc.codec.CommonEncoder;
import org.chw.rpc.enumeration.RpcError;
import org.chw.rpc.exception.RpcException;
import org.chw.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 用于远程连接获取Channel
 *
 * @Author CHW
 * @Date 2023/4/20
 **/
public class ChannelProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(ChannelProvider.class);
    
    private static EventLoopGroup eventLoopGroup;
    private static Bootstrap bootstrap = initializeBootstrap();
    
    private static final int MAX_RETRY_COUNT = 5;
    private static Channel channel = null;
    
    private static Bootstrap initializeBootstrap() {
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                //连接的超时时间，超过这个时间还是建立不上的话则代表连接失败
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                //是否开启 TCP 底层心跳机制
                .option(ChannelOption.SO_KEEPALIVE, true)
                //TCP默认开启了 Nagle 算法，该算法的作用是尽可能的发送大数据快，减少网络传输。TCP_NODELAY 参数的作用就是控制是否启用 Nagle 算法。
                .option(ChannelOption.TCP_NODELAY, true);
        return bootstrap;
    }
    
    /**
     * 获取Channel
     * @param inetSocketAddress 连接地址ip
     * @param serializer 序列化器
     * @return channel
     */
    public static Channel get(InetSocketAddress inetSocketAddress, CommonSerializer serializer) {
        //初始化bootstrap
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
                // RpcResponse -> ByteBuf
                ch.pipeline().addLast(new CommonEncoder(serializer))
                        .addLast(new CommonDecoder())
                        .addLast(new NettyClientHandler());
            }
        });
        //定义倒计时
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            //尝试连接
            connect(bootstrap, inetSocketAddress, countDownLatch);
            //等待连接结束
            countDownLatch.await();
        } catch (InterruptedException e) {
            logger.error("获取channel时有错误发生:", e);
        }
        return channel;
    }
    
    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, CountDownLatch countDownLatch) {
        connect(bootstrap, inetSocketAddress, MAX_RETRY_COUNT, countDownLatch);
    }
    
    private static void connect(Bootstrap bootstrap, InetSocketAddress inetSocketAddress, int retry, CountDownLatch countDownLatch) {
        //尝试连接绑定监听器
        bootstrap.connect(inetSocketAddress).addListener((ChannelFutureListener) future -> {
            //连接成功直接返回
            if (future.isSuccess()) {
                logger.info("客户端连接成功!");
                channel = future.channel();
                countDownLatch.countDown();
                return;
            }
            //重试失败
            if (retry == 0) {
                logger.error("客户端连接失败！");
                countDownLatch.countDown();
                throw new RpcException(RpcError.CLIENT_CONNECT_SERVER_FAILURE);
            }
            // 第几次重连
            int order = (MAX_RETRY_COUNT - retry) + 1;
            // 本次重连的间隔
            int delay = 1 << order;
            logger.error("{}: 连接失败，第 {} 次重连……", new Date(), order);
            //config返回BootstrapConfig对象，封装了Bootstrap配置信息。group()方法返回的是一个EventLoopGroup对象
            //schedule()方法则是在指定的延迟时间后执行给定的任务第一个参数是一个Runnable对象，它封装了要执行的任务；第二个参数是延迟时间；第三个参数是时间单位
            bootstrap.config().group().schedule(() -> connect(bootstrap, inetSocketAddress, retry - 1, countDownLatch), delay, TimeUnit
                    .SECONDS);
        });
    }
    
}

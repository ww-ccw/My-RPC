package org.chw.rpc.transport.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
import org.chw.rpc.entity.RpcResponse;
import org.chw.rpc.util.SingletonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Netty客户端处理器
 *
 * @Author CHW
 * @Date 2023/4/19
 **/
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
    
    private static final Logger logger = LoggerFactory.getLogger(NettyClientHandler.class);
    
    private final UnprocessedRequests unprocessedRequests;
    
    public NettyClientHandler() {
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }
    
    /**
     * 该方法是一个ChannelHandler处理RPC响应消息的回调方法。当通道接收到一个RpcResponse消息时
     *
     * @param ctx 当前通道的上下文对象
     * @param msg 经过解码的Response
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
        try{
            logger.info(String.format("客户端接受到消息:%s", msg));
            //将该请求移出未完成名单，并设置返回结果
            unprocessedRequests.complete(msg);
        }finally {
            // 释放消息引用计数器
            ReferenceCountUtil.release(msg);
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("过程调用时有错误发送:{}", cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}

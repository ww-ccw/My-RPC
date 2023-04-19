package org.chw.rpc.netty.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import org.chw.rpc.entity.RpcResponse;
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
            //创建了一个AttributeKey对象，指定其名称为"rpcResponse"
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse");
            //获取了通道channel的AttributeMap对象，并在其上调用set()方法将"rpcResponse"属性的值设置为msg
            ctx.channel().attr(key).set(msg);
            //关闭通道
            ctx.channel().close();
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

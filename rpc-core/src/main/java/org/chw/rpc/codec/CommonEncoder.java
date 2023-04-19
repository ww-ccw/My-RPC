package org.chw.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.enumeration.PackageType;
import org.chw.rpc.serializer.CommonSerializer;

/**
 * 通用编码拦截器
 *
 * @Author CHW
 * @Date 2023/4/19
 **/
public class CommonEncoder extends MessageToByteEncoder {
    
    
    /**
     * 魔数，用于标识传输的数据满足某种协议
     */
    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    
    /**
     * 序列化器
     */
    private final CommonSerializer serializer;
    
    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }
    
    /**
     * 在该方法中，按照特定的协议格式组织好要发送的二进制数据，并将其写入ByteBuf中，最终通过网络传输到远程节点。
     * +---------------+---------------+-----------------+-------------+
     * |  Magic Number |  Package Type | Serializer Type | Data Length |
     * |    4 bytes    |    4 bytes    |     4 bytes     |   4 bytes   |
     * +---------------+---------------+-----------------+-------------+
     * |                          Data Bytes                           |
     * |                   Length: ${Data Length}                      |
     * +---------------------------------------------------------------+
     * @param ctx 当前处理器的上下文
     * @param msg 编码对象
     * @param out Netty自带的可扩容缓冲区，用于存储编码后的二进制数据。
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeInt(MAGIC_NUMBER);
        
        if (msg instanceof RpcRequest){
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        }else {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        
        out.writeInt(serializer.getCode());
        
        byte[] bytes = serializer.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        
    }
}

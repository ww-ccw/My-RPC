package org.chw.rpc.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.entity.RpcResponse;
import org.chw.rpc.enumeration.PackageType;
import org.chw.rpc.enumeration.RpcError;
import org.chw.rpc.exception.RpcException;
import org.chw.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 通用解码拦截器
 *
 * @Author CHW
 * @Date 2023/4/19
 **/
public class CommonDecoder extends ReplayingDecoder {
    
    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;
    
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //验证魔数
        int magicNumber = in.readInt();
        if (magicNumber != MAGIC_NUMBER){
            logger.error("不识别的协议包: {}" , magicNumber);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        //验证数据包类型Request还是Response
        int packageCode = in.readInt();
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()){
            packageClass = RpcRequest.class;
        }else if(packageCode == PackageType.RESPONSE_PACK.getCode()){
            packageClass = RpcResponse.class;
        }else {
            logger.error("不识别的数据包:{}" , packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        //验证并得到序列化器
        int serializerCode = in.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null){
            logger.error("不识别的(反)序列化器:{}" , serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        //得到对象
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object obj = serializer.deserialize(bytes , packageClass);
        
        out.add(obj);
    }
}

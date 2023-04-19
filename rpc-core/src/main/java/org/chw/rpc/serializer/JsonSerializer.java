package org.chw.rpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 使用json格式的序列化器
 *
 * @Author CHW
 * @Date 2023/4/19
 **/
public class JsonSerializer implements CommonSerializer{
    
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);
    
    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public byte[] serialize(Object obj) {
        try{
            return objectMapper.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            logger.error("序列化时出错了:{}" , e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 将字节转换成对象
     */
    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if (obj instanceof RpcRequest){
                obj = handleRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            logger.error("反序列化时有错误发生:{}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 进一步验证该反序列化得到的对象是否是原实例类型。通过比较参数类型是否与声明类型匹配，如果不匹配则重新反序列化
     */
    private Object handleRequest(Object obj) throws IOException {
        RpcRequest rpcRequest = (RpcRequest) obj;
        for (int i = 0 ; i<rpcRequest.getParamTypes().length ; i++){
            Class<?> clazz = rpcRequest.getParamTypes()[i];
            if (!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())){
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i] = objectMapper.readValue(bytes , clazz);
            }
            
        }
        return obj;
    }
    
    @Override
    public int getCode() {
        return SerializerCode.valueOf("JSON").getCode();
    }
}

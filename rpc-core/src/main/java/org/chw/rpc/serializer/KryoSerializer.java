package org.chw.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import org.chw.rpc.entity.RpcRequest;
import org.chw.rpc.entity.RpcResponse;
import org.chw.rpc.enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * kryo(反)序列化器
 * @Author CHW
 * @Date 2023/4/20
 **/
public class KryoSerializer implements CommonSerializer{
    
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);
    
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() ->{
        
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
        
        return kryo;
    });
    
    
    @Override
    public byte[] serialize(Object obj) {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Output output = new Output(byteArrayOutputStream)){
            
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output , obj);
            kryoThreadLocal.remove();
            return output.toBytes();
            
        } catch (IOException e) {
            logger.error("序列化时出现错误", e);
            throw new SerializationException("序列化时有错误发生");
        }
    }
    
    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            Input input = new Input(byteArrayInputStream);
        ) {
            
            Kryo kryo = kryoThreadLocal.get();
            Object obj = kryo.readObject(input , clazz);
            kryoThreadLocal.remove();
            return obj;
            
        } catch (IOException e) {
            logger.error("反序列化时有错误发生:" , e);
            throw new SerializationException("反序列化时有错误发生");
        }
    }
    
    @Override
    public int getCode() {
        return SerializerCode.KRYO.getCode();
    }
}

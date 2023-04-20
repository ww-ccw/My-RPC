package org.chw.rpc.serializer;

/**
 * 通用的序列化数据接口
 * @Author CHW
 * @Date 2023/4/19
 **/
public interface CommonSerializer {
    /**
     * 将对象序列化为json格式字节
     */
    byte[] serialize(Object obj);
    
    /**
     * json格式字节转换成对象
     */
    Object deserialize(byte[] bytes , Class<?> clazz);
    
    /**
     * 得到(反)序列化器的代码
     */
    int getCode();
    
    static CommonSerializer getByCode(int code){
        switch (code){
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            default:
                return null;
        }
    }
    
}

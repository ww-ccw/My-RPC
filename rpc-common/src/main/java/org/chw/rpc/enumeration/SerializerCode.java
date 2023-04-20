package org.chw.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 字节流中标识序列化和反序列化器
 * @Author CHW
 * @Date 2023/4/19
 **/
@AllArgsConstructor
@Getter
public enum SerializerCode {
    KRYO(0),
    JSON(1),
    HESSIAN(2);
    
    private final int code;
}

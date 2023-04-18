package org.chw.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author CHW
 * @Date 2023/4/17
 *
 * 状态响应码
 **/
@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS(200 , "调用方法成功"),
    FAIL(500 , "调用方法失败"),
    METHOD_NOT_FOUND(500 , "未找到指定方法"),
    CLASS_NOT_FOUND(200 , "未找到类");
    
    
    private final int code;
    private final String message;
    
}

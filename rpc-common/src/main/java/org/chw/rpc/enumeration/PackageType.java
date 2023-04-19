package org.chw.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author CHW
 * @Date 2023/4/19
 **/
@AllArgsConstructor
@Getter
public enum PackageType {
    
    REQUEST_PACK(0),
    RESPONSE_PACK(1);
    
    private final int code;
}

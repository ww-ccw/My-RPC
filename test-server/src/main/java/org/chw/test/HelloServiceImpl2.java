package org.chw.test;

import org.chw.rpc.api.HelloObject;
import org.chw.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author CHW
 * @Date 2023/4/17
 **/
public class HelloServiceImpl2 implements HelloService {
    
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl2.class);
    
    @Override
    public String hello(HelloObject object) {
        logger.info("接受到: {}" , object.getMessage());
        return "本次使用Socket服务端";
    }
}

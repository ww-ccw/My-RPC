package org.chw.rpc.hook;

import org.chw.rpc.util.NacosUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * JVM关闭时的钩子，单例
 *
 * @Author CHW
 * @Date 2023/4/22
 **/
public class ShutdownHook {
    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);
    
    private static final ShutdownHook shutdownHook = new ShutdownHook();
    
    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }
    
    public void addClearAllHook() {
        logger.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("开始注销服务");
            NacosUtil.clearRegistry();
        }));
    }
}

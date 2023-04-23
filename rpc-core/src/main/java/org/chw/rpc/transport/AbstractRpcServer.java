package org.chw.rpc.transport;

import org.chw.rpc.annotation.Service;
import org.chw.rpc.annotation.ServiceScan;
import org.chw.rpc.enumeration.RpcError;
import org.chw.rpc.exception.RpcException;
import org.chw.rpc.provider.ServiceProvider;
import org.chw.rpc.registry.ServiceRegistry;
import org.chw.rpc.util.ReflectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * @Author CHW
 * @Date 2023/4/23
 **/
public abstract class AbstractRpcServer implements RpcServer{
    
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    
    protected String host;
    protected int port;
    
    protected ServiceRegistry serviceRegistry;
    protected ServiceProvider serviceProvider;
    
    public void scanServices(){
        String mainClassName = ReflectUtil.getStackTrace();
        Class<?> startClass;
        
        try{
            startClass = Class.forName(mainClassName);
            if (!startClass.isAnnotationPresent(ServiceScan.class)){
                logger.error("启动类缺少 @ServiceScan 注解");
                throw new RpcException(RpcError.SERVICE_SCAN_PACKAGE_NOT_FOUND);
            }
        } catch (ClassNotFoundException e) {
            logger.error("出现未知错误");
            throw new RpcException(RpcError.UNKNOWN_ERROR);
        }
    
        //获取ServiceScan的value，即要扫描的包名
        String basePackage = startClass.getAnnotation(ServiceScan.class).value();
        //如果未指定扫描包，则默认扫描启动类所在包下所有
        if ("".equals(basePackage)){
            basePackage = mainClassName.substring(0 , mainClassName.lastIndexOf("."));
        }
        //获取包下全部类对象集合
        Set<Class<?>> classSet = ReflectUtil.getClasses(basePackage);
        
        for (Class<?> clazz : classSet){
            if (clazz.getAnnotation(Service.class) == null) continue;
            String serviceName = clazz.getAnnotation(Service.class).name();
            Object obj;
            try{
                //获取服务实现类对象
                obj = clazz.newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                logger.error("创建"+clazz+"时有错误发生");
                continue;
            }
            if ("".equals(serviceName)){
                //获得该服务实现类实现的接口类集合
                Class<?>[] interfaces = clazz.getInterfaces();
                for (Class<?> oneInterface : interfaces){
                    //注册服务
                    publishService(obj , oneInterface.getCanonicalName());
                }
            }else {
                publishService(obj , serviceName);
            }
        }
    }
    
    @Override
    public <T> void publishService(T service, String serviceName) {
        serviceProvider.addServiceProvider(service, serviceName);
        serviceRegistry.register(serviceName, new InetSocketAddress(host, port));
    }
}

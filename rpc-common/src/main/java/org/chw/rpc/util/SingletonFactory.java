package org.chw.rpc.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 单例工厂
 *
 * @Author CHW
 * @Date 2023/4/22
 **/
public class SingletonFactory {
    private static Map<Class , Object> objectMap = new HashMap<>();
    
    private SingletonFactory(){}
    
    public static <T> T getInstance(Class<T> clazz){
        Object instance = objectMap.get(clazz);
        synchronized (clazz){
            if (instance == null){
                try{
                    instance = clazz.newInstance();
                    objectMap.put(clazz, instance);
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return clazz.cast(instance);
    }
    
}

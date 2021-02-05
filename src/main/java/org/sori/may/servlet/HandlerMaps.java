package org.sori.may.servlet;

import org.sori.may.annotation.MayController;
import org.sori.may.annotation.MayRequestMapping;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author May
 */
public class HandlerMaps extends DispatcherServlet {

    public void handlerMapss() {
        // 取出 IOC 容器中的所有 bean
        for (Map.Entry<String, Object> entry : iocBeans.entrySet()) {

            // 取出 bean
            Object instance = entry.getValue();

            // 拿到 Class对象
            Class<?> classObject = instance.getClass();

            // 判断控制类，只有控制类里面才能有请求路径
            if (classObject.isAnnotationPresent(MayController.class)) {
                // 拿到类上面的注解实体，因为这个注解的值是请求路径的第一步
                MayRequestMapping mayRequestMapping = classObject.getAnnotation(MayRequestMapping.class);

                // 拿到了注解类上面的路径 /may
                String classPath = mayRequestMapping.value();

                // 拿到方法上面的路径，因为类中有很多方法，所以这里要用数组来存起来
                Method[] methods = classObject.getMethods();
                assert methods != null;

                // 遍历出所有的方法
                for (Method method : methods) {
                    // 判断方法上面是否存在 @MayRequestMapping，我们的路径只会存在于有 @MayRequestMapping 的方法
                    if (method.isAnnotationPresent(MayRequestMapping.class)) {
                        // 首先拿到方法上的实例对象注解
                        MayRequestMapping requestMapping = method.getAnnotation(MayRequestMapping.class);

                        // 取出方法上的路径 /query
                        String methodPath = requestMapping.value();

                        // 将类的请求路径和 方法的请求路径合并起来，其中 key 就是我们合并的请求路径
                        // value 就是调用的方法，就是我们调用的方法对象
                        handlerMaps.put(classPath + methodPath, method);
                    }
                }
            }
        }
    }
}

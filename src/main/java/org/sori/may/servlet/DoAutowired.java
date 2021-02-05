package org.sori.may.servlet;

import org.sori.may.annotation.MayAutowired;
import org.sori.may.annotation.MayController;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * @author May
 */
public class DoAutowired extends DispatcherServlet{

    public void doAutowired() {
        // 把所有的 bean 遍历出来
        for (Map.Entry<String, Object> entry : iocBeans.entrySet()) {

            // 把 bean 拿出来
            Object instance = entry.getValue();

            // 拿到 Class 对象
            Class<?> classObject = instance.getClass();

            // 判断控制类中是否有 @MayAutowired 注解
            if (classObject.isAnnotationPresent(MayController.class)) {
                // 拿到类中的所有成员变量
                Field[] fields = classObject.getDeclaredFields();

                // 遍历出来所有的变量
                for (Field field : fields) {
                    // 判断哪个变量上用了 @Autowired 注解
                    if (field.isAnnotationPresent(MayAutowired.class)) {
                        /*
                           把变量对应的注解中的key对应的value拿出来 作为一个 key，到我们的IOC容器中拿
                           private SoriServiceImpl soriServiceImpl
                         */
                        // 拿到注解对应的实体
                        MayAutowired mayAutowired = field.getAnnotation(MayAutowired.class);
                        // 拿到 MayAutowired 中的 key 值
                        String controllerKey = mayAutowired.value();
                        // 根据这个 key 到容器中把 bean 取出来
                        Object object = iocBeans.get(controllerKey);

                        // MayAutowired 是 private 私有的，需要打开权限，不然没法用的
                        field.setAccessible(true);
                        try {
                            // 把刚刚从IOC容器中取出来的bean注入到 @MayAutowired 注解下面的 实例对象中
                            field.set(instance, object);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}

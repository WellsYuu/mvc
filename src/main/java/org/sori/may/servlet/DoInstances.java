package org.sori.may.servlet;

import org.sori.may.annotation.MayController;
import org.sori.may.annotation.MayRequestMapping;
import org.sori.may.annotation.MayService;

/**
 * @author May
 */
public class DoInstances extends DispatcherServlet{

    public void doInstances() {
        // 把刚刚扫描出来的 *.class 进行遍历
        for (String className : classNames) {
            // 拿出来的是 xx.xx.xx.controller.class 是不能被实例化的
            String removeClass = className.replace(".class", "");
            try {
                // 拿到 Class 对象
                Class<?> classObject = Class.forName(removeClass);

                // 只有声明了 @MayController 和 @MayService 的类才能进行实例化
                if (classObject.isAnnotationPresent(MayController.class)) {
                    /*
                      被遍历出来的是控制类，因为声明了 @MayController 注解
                      这里先创建控制类对象
                      说明：IOC 容器是一个大的MAP对象 IOCMAP.put(key, value)
                      这里的 key 是我们自己定义的大写（MayController）
                      这里的 value 就是 instance
                     */
                    Object instance = classObject.newInstance();

                    // 拿到类上面的注解信息
                    MayRequestMapping mayRequestMappingController = classObject.getAnnotation(MayRequestMapping.class);

                    // 将 value 作为 key（key: /may）
                    String controllerKey = mayRequestMappingController.value();

                    // 把 key 和 value 写入 IOC
                    iocBeans.put(controllerKey, instance);
                } else if (classObject.isAnnotationPresent(MayService.class)) {
                    /*
                      被遍历出来的是 业务逻辑类，因为声明了 @MayService 注解
                      这里先创建业务逻辑类对象
                      说明：IOC 容器是一个大的MAP对象 IOCMAP.put(key, value)
                      这里的 key 是我们自己定义的大写（MayService）
                      这里的 value 就是 instance
                     */
                    Object instance = classObject.newInstance();

                    // 拿到类上面的注解信息
                    MayService mayService = classObject.getAnnotation(MayService.class);

                    // 将 value 作为 key（key: /SoriServiceImpl）
                    String serviceKey = mayService.value();

                    // 把 key 和 value 写入 IOC
                    iocBeans.put(serviceKey, instance);
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}

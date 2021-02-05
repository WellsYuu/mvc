package org.sori.may.annotation;

import java.lang.annotation.*;

/**
 * 1. 这个注解只能作用于 类上
 * 2. 在 Tomcat 运行的时候通过反射拿到注解的相关信息
 *
 * Service
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MayService {
    String value() default "";
}

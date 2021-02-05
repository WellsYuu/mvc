package org.sori.may.annotation;

import java.lang.annotation.*;

/**
 * 1. 这个注解只能作用于 方法参数中
 * 2. 在 Tomcat 运行的时候通过反射拿到注解的相关信息
 *
 * RequestParam
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MayRequestParam {
    String value() default "";
}

package org.sori.may.servlet;

import org.sori.may.annotation.*;
import org.sori.may.controller.SoriController;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Sori
 * <p>
 * DispatcherServlet 具体实现
 */
public class DispatcherServlet extends HttpServlet {

    /**
     * 存放 *.class
     */
    public static List<String> classNames = new ArrayList<>();

    /**
     * IOC 容器
     */
    public static ConcurrentHashMap<String, Object> iocBeans = new ConcurrentHashMap<>();

    /**
     * 映射路径
     */
    public static ConcurrentHashMap<String, Object> handlerMaps = new ConcurrentHashMap<>();

    @Override
    public void init(ServletConfig config) {

        /*
          1. 进行包扫描
         */
        ScanPackages scanPackages = new ScanPackages();
        scanPackages.scanPackage("org.sori");

        /*
           2. 把刚刚扫描出来的 *.class 文件进行实例化，IOC 的初始化
         */
        DoInstances doInstances = new DoInstances();
        doInstances.doInstances();

        /*
           3. bean 与 bean 之间的关系，通过 Autowired 实现自动注入 DI
         */
        DoAutowired doAutowired = new DoAutowired();
        doAutowired.doAutowired();

        /*
           4. 凭什么输入请求地址后就能找到对应的控制类 ？
           提交请求的时候对应了一个 method方法，请求路径是怎么对应method方法的 ？
           在 Spring MVC 中用的是 URLHandlerMapping
           key 为 /may/query  value 为 query() 的方法 method.put(key, value)
         */
        HandlerMaps handlerMapss = new HandlerMaps();
        handlerMapss.handlerMapss();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    /**
     * 处理业务
     * 请求的时候，把相应的请求路径、数据转交给 doGet或者doPost
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // HTTP 请求过来，先拿到路径,项目名/may-mvc/may/query，包括了我们不需要的路径，我们只需要类中的请求路径
        String uri = req.getRequestURI();

        // 拿到这个不需要的路径 xx/xx/may-mvc
        String removeContext = req.getContextPath();

        // 去掉后的路径 /may/query
        String path = uri.replace(removeContext, "");

        // 拿到路径对应的方法
        Method method = (Method) handlerMaps.get(path);

        // 方法对应的 Controller拿出来,到 IOC 容器中去拿，只拿第一个 拿到的就是 /may 这个 key，在IOC容器中找到对应的bean
        SoriController mayControllerInstance = (SoriController) iocBeans.get("/" + path.split("/")[1]);

        // 处理 Param 参数
        Object object[] = param(req, resp, method);

        // 最后直接调用就 ok了
        try {
            method.invoke(mayControllerInstance, object);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理方法中的参数
     * @param request
     * @param response
     * @param method
     * @return
     */
    private static Object[] param(HttpServletRequest request, HttpServletResponse response, Method method) {
        // 拿到当前待执行的方法有哪些参数类型
        Class<?>[] paramObject = method.getParameterTypes();
        // 根据参数的个数 new 一个参数的数组 将方法里的所有参数赋值给 args
        Object[] args = new Object[paramObject.length];

        int paramNo = 0;
        int annotationNo = 0;

        // 判断参数类型
        for (Class<?> paramClazz : paramObject) {
            // 第一个 request 参数类型
            if (ServletRequest.class.isAssignableFrom(paramClazz)) {
                args[paramNo++] = request;
            }

            // 第二个 response 参数类型
            if (ServletResponse.class.isAssignableFrom(paramClazz)) {
                args[paramNo++] = response;
            }

            // 判断是否是 @MayRequestParam 注解
            Annotation[] paramAns = method.getParameterAnnotations()[annotationNo];
            if (paramAns.length > 0) {

                // 将注解参数遍历
                for (Annotation paramAn : paramAns) {
                    // 判断是否有 @MayRequestParam 注解
                    if (MayRequestParam.class.isAssignableFrom(paramAn.getClass())) {
                        // 拿到 注解对象
                        MayRequestParam requestParam = (MayRequestParam) paramAn;
                        // 找到注解中的参数
                        args[paramNo++] = request.getParameter(requestParam.value());
                    }
                }
            }
            annotationNo++;
        }
        return args;
    }
}

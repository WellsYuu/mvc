package org.sori.may.controller;

import org.sori.may.annotation.MayAutowired;
import org.sori.may.annotation.MayController;
import org.sori.may.annotation.MayRequestMapping;
import org.sori.may.annotation.MayRequestParam;
import org.sori.may.service.SoriService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Sori
 * SoriController 控制层
 */
@MayController
@MayRequestMapping(value = "/may")
public class SoriController {

    /**
     * 从 IOCMap 获取 bean 的实例的时候
     * 这个key我们就从 MayService 中定义的value中去拿
     * 并把这个 object 的实例赋值到了 soriService 中，就完成了DI的注入
     */
    @MayAutowired("SoriServiceImpl")
    private SoriService soriService;

    @MayRequestMapping(value = "/query")
    public void queryAll(HttpServletRequest request, HttpServletResponse response,
                         @MayRequestParam("frameworkName") String frameworkName,
                         @MayRequestParam("author") String author,
                         @MayRequestParam("version") String version) throws IOException {

        String queryResult = soriService.queryAll(frameworkName, author, version);

        /*
          打印到浏览器
         */
        PrintWriter printWriter = response.getWriter();
        printWriter.write(queryResult);

    }
}

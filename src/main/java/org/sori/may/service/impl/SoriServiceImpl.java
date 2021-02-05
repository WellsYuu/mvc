package org.sori.may.service.impl;

import org.sori.may.annotation.MayService;
import org.sori.may.service.SoriService;

/**
 * @author Sori
 * SoriService 实现层
 * Spring MVC 中默认以小写的实例对象作为 key，既然要手写，那就全部自定义
 */
@MayService("SoriServiceImpl")
public class SoriServiceImpl implements SoriService {
    @Override
    public String queryAll(String frameworkName, String author, String version) {
        return " Framework name: " + frameworkName +
                " &Author: " + author +
                " &version: " + version;
    }
}

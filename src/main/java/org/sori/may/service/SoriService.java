package org.sori.may.service;

/**
 * @author Sori
 * 测试业务代码接口
 */
public interface SoriService {
    /**
     * 便于测试就传3个参数
     * @param frameworkName
     * @param author
     * @param version
     * @return
     */
    String queryAll(String frameworkName, String author, String version);
}

package org.sori.may.servlet;

import java.io.File;
import java.net.URL;

/**
 * @author May
 */
public class ScanPackages extends DispatcherServlet{

    public void scanPackage(String basePackage) {
        // 拿到 C://xx/xx 替换成 xx.xx.xx

        URL url = this.getClass().getClassLoader().getResource(
                "/" + basePackage.replaceAll("\\.", "/"));
        assert url != null;

        // 拿到路径
        String fileLocation = url.getFile();
        // 转换成文件对象
        File file = new File(fileLocation);
        // 拿到文件下的所有路径或者文件名
        String[] allFile = file.list();

        assert allFile != null;
        // 遍历出来
        for (String filePath : allFile) {
            // 先拿到全路径并转换为文件
            File getFilePath = new File(fileLocation + filePath);

            // 判断当前拿到的文件是不是文件夹
            if (getFilePath.isDirectory()) {
                // 如果是文件夹，那就继续遍历判断，进行递归
                scanPackage(basePackage + "." + filePath);
            } else {
                // 如果不是文件夹，那就是 *.class 结尾的类，把这些 *.class 存起来 (org.sori.may.xx.controller.class)
                classNames.add(basePackage + "." + getFilePath.getName());
            }
        }
    }
}

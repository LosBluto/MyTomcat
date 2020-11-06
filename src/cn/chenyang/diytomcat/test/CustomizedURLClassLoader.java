package cn.chenyang.diytomcat.test;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Author: chenyang
 * Time: 2020/11/2
 * Description: 从jar包总加载class，因为jar中会有多个class
 */
public class CustomizedURLClassLoader extends URLClassLoader {
    public CustomizedURLClassLoader(URL[] urls) {
        super(urls);
    }

    public static void main(String[] args) throws Exception{
        URL url = new URL("file:d:/project/diytomcat/jar_4_test/test.jar");
        URL[] urls = new URL[] {url};

        CustomizedURLClassLoader loader = new CustomizedURLClassLoader(urls);

        Class<?> how2jClass = loader.loadClass("cn.how2j.diytomcat.test.HOW2J");

        Object o = how2jClass.newInstance();
        Method m = how2jClass.getMethod("hello");
        m.invoke(o);

        System.out.println(how2jClass.getClassLoader());
    }
}

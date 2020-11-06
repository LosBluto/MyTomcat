package cn.chenyang.diytomcat;

import cn.chenyang.diytomcat.classloader.CommonClassLoader;
import java.lang.reflect.Method;

/**
 * Author: chenyang
 * Time: 2020/8/21
 * Description:
 */
public class BootStrap {

    /*
    在启动设置中加入了 -XX:+TraceClassLoading ，能够追踪加载的jar包
     */
    public static void main(String[] args) throws Exception {
        CommonClassLoader commonClassLoader = new CommonClassLoader();
        //bootstrap源码中的，将该classloader加入到当前进程中，方便之后调用
        Thread.currentThread().setContextClassLoader(commonClassLoader);

        String className = "cn.chenyang.diytomcat.catalina.Server";
        Class<?> clazz = commonClassLoader.loadClass(className);

        Object serverObject = clazz.newInstance();
        Method method = clazz.getMethod("start");
        method.invoke(serverObject);

        System.out.println(clazz.getClassLoader());
    }






}

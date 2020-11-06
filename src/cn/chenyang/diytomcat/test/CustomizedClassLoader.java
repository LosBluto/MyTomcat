package cn.chenyang.diytomcat.test;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.security.acl.LastOwnerException;

/**
 * Author: chenyang
 * Time: 2020/11/2
 * Description: 只能加载单个class的加载器
 */
public class CustomizedClassLoader extends ClassLoader {
    private File classesFolder = new File(System.getProperty("user.dir"),"classes_4_test");    //获取存放测试文件的目录

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] data  = loadClassData(name);                 //用文件名查找file转成bytes
        return defineClass(name,data,0,data.length);    //再把其转为class
    }

    private byte[] loadClassData(String name) throws ClassNotFoundException {
        String fileName = StrUtil.replace(name,".","/")+".class";
        File classFile = new File(classesFolder,fileName);
        if (!classFile.exists())
            throw new ClassNotFoundException(name);
        return FileUtil.readBytes(classFile);
    }

    public static void main(String[] args) throws Exception{
        CustomizedClassLoader loader = new CustomizedClassLoader();
        Class<?> myClass = loader.findClass("cn.chenyang.diytomcat.HOW2J");

        //利用反射机制加载类
        Object object = myClass.newInstance();
        Method m = myClass.getMethod("hello");
        m.invoke(object);
        System.out.println(myClass.getClassLoader());
    }
}

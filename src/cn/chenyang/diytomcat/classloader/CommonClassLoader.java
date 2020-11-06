package cn.chenyang.diytomcat.classloader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Author: chenyang
 * Time: 2020/11/3
 * Description: 加载本低项目中的servlet
 */
public class CommonClassLoader extends URLClassLoader {
    public CommonClassLoader() {
        super(new URL[]{});

        try {
            File workingFolder = new File(System.getProperty("user.dir"));
            File libFolder = new File(workingFolder,"lib");
            File[] jarFiles = libFolder.listFiles();

            for (File jar:jarFiles){                    //把lib文件夹中jar文件路径添加到加载器中
                if (jar.getName().endsWith("jar")){
                    URL url = new URL("file:"+jar.getAbsolutePath());
                    this.addURL(url);
                }
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


}

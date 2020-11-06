package cn.chenyang.diytomcat.classloader;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

/**
 * Author: chenyang
 * Time: 2020/11/5
 * Description: 加载javaweb项目的servlet
 */
public class webappClassLoader extends URLClassLoader {
    public webappClassLoader(String docBase, ClassLoader parent) {
        super(new URL[]{}, parent);

        try {
            File webinfFolder = new File(docBase, "WEB-INF");       //web-inf文件夹
            File classesFolder = new File(webinfFolder, "classes"); //classes文件夹
            File libFolder = new File(webinfFolder,"lib");
            URL url;
            url = new URL("file:"+classesFolder.getAbsolutePath()+"/"); //因为是获取classes这个文件夹中的内容，所以需要加/
            this.addURL(url);

            List<File> jarFiles = FileUtil.loopFiles(libFolder);            //加载lib中的jar包
            for (File file: jarFiles){
                url = new URL("file:"+file.getAbsolutePath());
                this.addURL(url);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        try {
            close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

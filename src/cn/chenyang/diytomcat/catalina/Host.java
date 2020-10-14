package cn.chenyang.diytomcat.catalina;


import cn.chenyang.diytomcat.utils.Constant;
import cn.chenyang.diytomcat.utils.ServerXmlUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: chenyang
 * Time: 2020/9/16
 * Description: 把映射信息放入主机中
 */
public class Host {
    private String name;
    private Map<String,Context> contextMap;
    private Engine engine;

    public Host(String name,Engine engine) {
        this.name = name;
        this.engine = engine;
        this.contextMap = new HashMap<>();

        scanContextInServerXML();
        scanContextsOnWebAppsFolder();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Context getContextMap(String path) {
        return contextMap.get(path);
    }

    /*
            扫描配置文件中的context
             */
    private  void scanContextInServerXML(){
        List<Context> contexts = ServerXmlUtil.getContexts();
        for (Context context:contexts){
            contextMap.put(context.getPath(),context);
        }
    }

    /*
    初始化context映射关系
     */
    private  void scanContextsOnWebAppsFolder(){
        File[] folders = Constant.webappsFolder.listFiles();    //获取webapps目录下所有文件
        for (File folder: folders){
            if (!(folder.isDirectory()))    //不是文件夹跳过
                continue;
            loadContext(folder);            //是文件夹则简历映射
        }
    }

    /*
    把映射加入到contextMap中
     */
    private void loadContext(File folder){
        String path = folder.getName();
        if ("ROOT".equals(path))
            path = "/";
        else
            path = "/" + path;
        String docBase = folder.getAbsolutePath();
        Context context = new Context(path,docBase);
        contextMap.put(path,context);
    }
}

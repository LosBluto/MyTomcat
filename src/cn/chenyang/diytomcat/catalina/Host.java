package cn.chenyang.diytomcat.catalina;


import cn.chenyang.diytomcat.utils.Constant;
import cn.chenyang.diytomcat.utils.ServerXmlUtil;
import cn.hutool.log.LogFactory;

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
    private Engine engine;

    private Map<String,Context> contextMap;

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

    public void reload(Context context){
        LogFactory.get().info("Reloading Context with name [{}] has started", context.getPath());
        String path = context.getPath();
        String docBase = context.getDocBase();
        boolean reloadable = context.isReloadable();

        //关闭为改变的context
        context.stop();
        //从host的映射中移除
        contextMap.remove(path);
        //增加改变后的context
        Context newContext = new Context(path,docBase,this,reloadable);
        //再把新的context加入映射中
        contextMap.put(newContext.getPath(),newContext);
        LogFactory.get().info("Reloading Context with name [{}] has completed", context.getPath());
    }

    /*
            扫描配置文件中的context
             */
    private  void scanContextInServerXML(){
        List<Context> contexts = ServerXmlUtil.getContexts(this);
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
        Context context = new Context(path,docBase,this,true);
        contextMap.put(path,context);
    }
}

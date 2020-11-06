package cn.chenyang.diytomcat.catalina;

import cn.chenyang.diytomcat.classloader.webappClassLoader;
import cn.chenyang.diytomcat.exception.WebConfigDuplicatedException;
import cn.chenyang.diytomcat.utils.Constant;
import cn.chenyang.diytomcat.utils.ContextXmlUtil;
import cn.chenyang.diytomcat.watcher.ContextFileChangeWatcher;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import com.sun.xml.internal.bind.v2.TODO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.*;

/**
 * Author: chenyang
 * Time: 2020/9/14
 * Description: 用于确认应用的路径
 */
public class Context {
    private String path;
    private String docBase;

    private File contextWebXmlFile;
    private webappClassLoader webappClassLoader;            //每个javaweb应用应该对应了一个classloader来加载


    private Host host;
    private boolean reloadable;
    private ContextFileChangeWatcher contextFileChangeWatcher;  //监听器，监听该Context对应的应用
    /*
    四个映射
     */
    private Map<String, String> url_servletClassName;
    private Map<String, String> url_servletName;
    private Map<String, String> servletName_className;
    private Map<String, String> className_servletName;

    public Context(String path, String docBase,Host host,boolean reloadable) {
        this.host = host;
        this.reloadable = reloadable;

        TimeInterval timeInterval = DateUtil.timer();           //计时便于输出日志
        contextWebXmlFile = new File(docBase, ContextXmlUtil.getWatchedResource());
        this.path = path;
        this.docBase = docBase;
        this.url_servletClassName = new HashMap<>();
        this.url_servletName = new HashMap<>();
        this.servletName_className = new HashMap<>();
        this.className_servletName = new HashMap<>();

        ClassLoader commonClassloader = Thread.currentThread().getContextClassLoader();     //获取在bootstrap中设置的classloader作为父classloader
        webappClassLoader = new webappClassLoader(docBase,commonClassloader);

        deploy();
        LogFactory.get().info("Deploying web application directory {}", this.docBase);
        LogFactory.get().info("Deployment of web application directory {} has finished in {} ms",
                this.docBase,timeInterval.intervalMs());
    }

    /*
    初始化四个映射
     */
    private void parseServletMapping(Document d){
        //url_servletName
        Elements mappingurlElements = d.select("servlet-mapping url-pattern");
        for (Element mappingurlElement:mappingurlElements){
            String urlPattern = mappingurlElement.text();
            String servletName = mappingurlElement.parent().select("servlet-name").first().text();
            url_servletName.put(urlPattern,servletName);
        }

        //servletName_className     className_servletName
        Elements servletNameElements = d.select("servlet servlet-name");
        for (Element servletNameElement:servletNameElements){
            String servletName = servletNameElement.text();
            String servletClass = servletNameElement.parent().select("servlet-class").first().text();
            servletName_className.put(servletName,servletClass);
            className_servletName.put(servletClass,servletName);
        }

        //url_servletClassName
        Set<String> urls = url_servletName.keySet();
        for (String url:urls){
            String servletName = url_servletName.get(url);
            String servletClassName = servletName_className.get(servletName);
            url_servletClassName.put(url,servletClassName);
        }
    }

    /*
    去除重复
     */
    private void checkDuplicated(Document d,String mapping,String desc)throws WebConfigDuplicatedException{
        Elements elements = d.select(mapping);

        List<String> contents = new ArrayList<>();      //利用集合排序后比较是否相同
        for (Element e:elements){
            contents.add(e.text());
        }
        Collections.sort(contents);
        for (int i = 0;i<contents.size()-1;i++){
            if (contents.get(i).equals(contents.get(i+1)))
                throw new WebConfigDuplicatedException(StrUtil.format(desc,contents.get(i)));
        }
    }

    private void checkDuplicated() throws WebConfigDuplicatedException{
        String xml = FileUtil.readUtf8String(Constant.contextXmlFile);
        Document d = Jsoup.parse(xml);
        checkDuplicated(d,"servlet-mapping url-pattern","servlet url 重复,请保持其唯一性:{} ");
        checkDuplicated(d, "servlet servlet-name", "servlet 名称重复,请保持其唯一性:{} ");
        checkDuplicated(d, "servlet servlet-class", "servlet 类名重复,请保持其唯一性:{} ");
    }

    public void init(){
        if(!contextWebXmlFile.exists())     //文件不存在
            return;
        try {
            checkDuplicated();
        }catch (WebConfigDuplicatedException e){
            e.printStackTrace();
            return;
        }
        String xml = FileUtil.readUtf8String(contextWebXmlFile);
        Document d = Jsoup.parse(xml);

        parseServletMapping(d);         //初始化映射
    }

    /*
    初始化
     */
    private void deploy(){
        TimeInterval timeInterval = DateUtil.timer();       //计时器
        LogFactory.get().info("Deploying web application directory {}",docBase);
        init();
        if (reloadable){
            contextFileChangeWatcher = new ContextFileChangeWatcher(this);
            contextFileChangeWatcher.start();           //开启监听
        }
        LogFactory.get().info("Deployment of web application directory {} has finished in {} ms",docBase,timeInterval.intervalMs());
    }

    /*
        停止，停止该应用的类加载器工作和监听器工作
     */
    public void stop(){
        webappClassLoader.stop();
        contextFileChangeWatcher.stop();
    }

    public void reload(){
        host.reload(this);
    }

    public String getServletClassName(String uri){
        return url_servletClassName.get(uri);
    }


    public void setPath(String path) {
        this.path = path;
    }

    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }

    public String getPath() {
        return path;
    }

    public String getDocBase() {
        return docBase;
    }

    public cn.chenyang.diytomcat.classloader.webappClassLoader getWebappClassLoader() {
        return webappClassLoader;
    }

    public void setReloadable(boolean reloadable) {
        this.reloadable = reloadable;
    }

    public boolean isReloadable() {
        return reloadable;
    }
}

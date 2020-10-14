package cn.chenyang.diytomcat.catalina;

import cn.chenyang.diytomcat.exception.WebConfigDuplicatedException;
import cn.chenyang.diytomcat.utils.Constant;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
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

    /*
    四个映射
     */
    private Map<String, String> url_servletClassName;
    private Map<String, String> url_servletName;
    private Map<String, String> servletName_className;
    private Map<String, String> className_servletName;

    public Context(String path, String docBase) {
        TimeInterval timeInterval = DateUtil.timer();           //计时便于输出日志
        this.path = path;
        this.docBase = docBase;
        this.url_servletClassName = new HashMap<>();
        this.url_servletName = new HashMap<>();
        this.servletName_className = new HashMap<>();
        this.className_servletName = new HashMap<>();
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
}

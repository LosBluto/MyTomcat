package cn.chenyang.diytomcat.utils;

import cn.chenyang.diytomcat.catalina.*;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: chenyang
 * Time: 2020/9/15
 * Description: 获取配置文件中的context
 */
public class ServerXmlUtil {
    public static String xml = FileUtil.readUtf8String(Constant.serverXmlFile);

    public static List<Context> getContexts(){
        List<Context> result = new ArrayList<>();
//        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);              //利用jsoup转换 提取信息

        Elements es = d.select("Context");
        for (Element e: es){
            String path = e.attr("path");
            String docBase = e.attr("docBase");
            Context context = new Context(path,docBase);
            result.add(context);
        }

        return result;
    }

    /*
    获取host的名字
     */
    public static String getServiceName(){
//        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);

        Element e = d.select("Service").first();
        return e.attr("name");
    }

    /*
    从xml中获取engine的默认host
     */
    public static String getEngineDefaultHost(){
//        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);

        Element e = d.select("Engine").first();
        return e.attr("defaultHost");
    }

    public static List<Host> getHosts(Engine engine){
        Document d = Jsoup.parse(xml);
        List<Host> hosts = new ArrayList<>();

        Elements es = d.select("Host");
        for (Element e: es){
            String name = e.attr("name");
            Host host = new Host(name,engine);
            hosts.add(host);
        }

        return hosts;
    }

    /*
    从xml中获取连接
     */
    public static List<Connector> getConnectors(Service service){
        List<Connector> result = new ArrayList<>();
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);
        Elements es = d.select("Connector");
        for (Element e: es){
            int port = Convert.toInt(e.attr("port"));
            Connector connector = new Connector(service);
            connector.setPort(port);
            result.add(connector);
        }

        return result;
    }
}

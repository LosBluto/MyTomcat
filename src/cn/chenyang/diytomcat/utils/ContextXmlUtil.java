package cn.chenyang.diytomcat.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


/**
 * Author: chenyang
 * Time: 2020/10/11
 * Description:
 */
public class ContextXmlUtil {

    public  static String getWatchedResource(){
        try {
            String xml = FileUtil.readUtf8String(Constant.contextXmlFile);
            Document d = Jsoup.parse(xml);
            Element e = d.select("WatchedResource").first();
            return e.text();
        }catch (Exception e){
            e.printStackTrace();
            return "WEB-INF/web.xml";
        }
    }
}

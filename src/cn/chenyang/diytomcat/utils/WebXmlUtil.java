package cn.chenyang.diytomcat.utils;

import cn.chenyang.diytomcat.catalina.Context;
import cn.hutool.core.io.FileUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: chenyang
 * Time: 2020/9/28
 * Description:
 */
public class WebXmlUtil {
    /*
    记录后缀对应的类型
     */
    private static Map<String, String> mimeTypeMapping = new HashMap<>();

    /*
    初始化xml中type的信息
     */
    private static void initMimeType(){
        String xml = FileUtil.readUtf8String(Constant.webXmlFile);
        Document d = Jsoup.parse(xml);

        Elements es = d.select("mime-mapping");
        for (Element e:es){
            String extName = e.select("extension").first().text();
            String mimeType = e.select("mime-type").first().text();
            mimeTypeMapping.put(extName,mimeType);
        }
    }

    /*
        添加线程同步，防止mimeTypeMapping被重复初始化
     */
    public static synchronized String getMimeType(String extName){
        if (mimeTypeMapping.isEmpty())
            initMimeType();
        String mimeType = mimeTypeMapping.get(extName);
        if (mimeType == null)
            return "text/html";
        return mimeType;

    }

    public static String getWelComeFile(Context context){
        String xml = FileUtil.readUtf8String(Constant.serverXmlFile);
        Document d = Jsoup.parse(xml);
        Elements es = d.select("welcome-file");
        for (Element e:es){
            String welcomeName = e.text();
            File file = new File(context.getDocBase(),welcomeName);
            if (file.exists())
                return file.getName();
        }

        return "index.html";
    }


}

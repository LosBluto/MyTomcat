package cn.chenyang.diytomcat.catalina;

import cn.chenyang.diytomcat.http.Request;
import cn.chenyang.diytomcat.http.Response;
import cn.chenyang.diytomcat.utils.Constant;
import cn.chenyang.diytomcat.utils.ThreadPoolUtil;
import cn.chenyang.diytomcat.utils.WebXmlUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.Struct;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Author: chenyang
 * Time: 2020/9/23
 * Description:
 */
public class Server {
    private Service service;

    public Server() {
        this.service = new Service(this);
    }

    public void start(){
        TimeInterval timeInterval = DateUtil.timer();
        logJVM();
        init();
        LogFactory.get().info("Server startup in {} ms",timeInterval.intervalMs());     //开启整个server的时间
    }

    private void init(){
        service.start();
    }

    /*
   配置logger
    */
    private static void logJVM(){
        Map<String, String> infos = new LinkedHashMap<>();
        infos.put("Server version","Jiang DiyTomcat/1.0.1");
        infos.put("Server built time","2020-09-11 13:49:22");
        infos.put("OS Name\t", SystemUtil.get("os.name"));
        infos.put("OS Version",SystemUtil.get("os.version"));
        infos.put("Architecture",SystemUtil.get("os.arch"));
        infos.put("Java Home",SystemUtil.get("java.home"));
        infos.put("JVM Version",SystemUtil.get("java.runtime.version"));
        infos.put("JVM Vendor",SystemUtil.get("java.vm.specification,vendor"));

        Set<String> keys = infos.keySet();
        for (String key: keys){
            LogFactory.get().info(key+":\t\t"+infos.get(key));      //输出日志
        }
    }


}

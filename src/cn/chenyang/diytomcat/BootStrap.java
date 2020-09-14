package cn.chenyang.diytomcat;

import cn.chenyang.diytomcat.http.Response;
import cn.chenyang.diytomcat.utils.Constant;
import cn.chenyang.diytomcat.catalina.Context;
import cn.chenyang.diytomcat.utils.ThreadPoolUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.chenyang.diytomcat.http.Request;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import cn.hutool.system.SystemUtil;


import java.io.File;
import java.io.IOException;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Author: chenyang
 * Time: 2020/8/21
 * Description:
 */
public class BootStrap {
    public static Map<String, Context> contextMap = new HashMap<>();        //存放路径与context的映射,便于查找

    public static void main(String[] args){
        try {
            lgoJVM();

            int port = 18080;
//            if (!NetUtil.isUsableLocalPort(port)) {
//                System.err.printf("端口%d已占用", port);
//                return;
//            }
            ServerSocket ss = new ServerSocket(port);
            while (true){
                Socket s = ss.accept();                     //获取套接字

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Request request = new Request(s);
                            String requestString = request.getRequestString();
                            System.out.println("输出的信息为:\r\n" + requestString);
                            System.out.println("输出的Uri为:\r\n" + request.getUri());

                            Response response = new Response();
                            String uri = request.getUri();                          //获取uri
                            if (null == uri)
                                return;
                            System.out.println(uri);

                            if ("/".equals(uri)) {                                  //无uri
                                String html = "Hello DIY Tomcat from how2j.cn";
                                response.getPrintWriter().println(html);
                            } else {                                                 //有uri寻找相应的文件
                                String fileName = StrUtil.removePrefix(uri, "/");
                                File file = FileUtil.file(Constant.rootFolder, fileName);

                                if (fileName.equals("timeConsume.html"))                    //模拟耗时，三秒延迟
                                    ThreadUtil.sleep(1000);

                                if (file.exists()) {
                                    String fileContent = FileUtil.readUtf8String(file);
                                    response.getPrintWriter().println(fileContent);
                                } else
                                    response.getPrintWriter().println("File Not Found");
                            }
                            handle200(s, response);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };

                ThreadPoolUtil.run(runnable);
            }

        }catch (IOException e){
            LogFactory.get().error(e);          //输出错误信息
            e.printStackTrace();
        }

    }

    /*
    初始化context映射关系
     */
    private static void scanContextsOnWebAppsFolder(){
        File[] folders = Constant.webappsFolder.listFiles();    //获取webapps目录下所有文件
        for (File folder: folders){
            if (!(folder.isDirectory()))    //不是文件夹跳过
                continue;

        }
    }

    /*
    把映射加入到contextMap中
     */
    private static void loadContext(File folder){

    }

    /*
    配置logger
     */
    private static void lgoJVM(){
        Map<String, String> infos = new LinkedHashMap<>();
        infos.put("Server version","Jiang DiyTomcat/1.0.1");
        infos.put("Server built time","2020-09-11 13:49:22");
        infos.put("OS Name\t",SystemUtil.get("os.name"));
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

    private static void handle200(Socket s, Response response) throws IOException {
        String contentType = response.getContentType();
        String headText = Constant.response_head_202;
        headText = StrUtil.format(headText,contentType);

        byte[] head = headText.getBytes();
        byte[] body = response.getBody();

        byte[] responseBytes = new byte[head.length+ body.length];
        ArrayUtil.copy(head,0,responseBytes,0,head.length);
        ArrayUtil.copy(body,0,responseBytes,head.length,body.length);

        OutputStream os = s.getOutputStream();
        os.write(responseBytes);
        os.flush();
        s.close();
    }
}

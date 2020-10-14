package cn.chenyang.diytomcat.catalina;

import cn.chenyang.diytomcat.http.Request;
import cn.chenyang.diytomcat.http.Response;
import cn.chenyang.diytomcat.utils.Constant;
import cn.chenyang.diytomcat.utils.ThreadPoolUtil;
import cn.chenyang.diytomcat.utils.WebXmlUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Author: chenyang
 * Time: 2020/10/8
 * Description:
 */
public class Connector implements Runnable {
    int port;
    private Service service;

    public Connector(Service service) {
        this.service = service;
    }

    public Service getService() {
        return service;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void init() {
        LogFactory.get().info("Initializing ProtocolHandler [http-bio-{}]",port);
    }

    public void start(){
        LogFactory.get().info("Starting ProtocolHandler [http-bio-{}]",port);
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {

            ServerSocket ss = new ServerSocket(port);
            while (true){
                Socket s = ss.accept();                     //获取套接字
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Request request = new Request(s, service);
                            Response response = new Response();
                            HttpProcessor httpProcessor = new HttpProcessor();
                            httpProcessor.execute(s,request,response);
                        } catch (IOException e) {
                            LogFactory.get().error(e);
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


}

package cn.chenyang.diytomcat.http;

import cn.chenyang.diytomcat.BootStrap;
import cn.chenyang.diytomcat.catalina.Context;
import cn.chenyang.diytomcat.catalina.Engine;
import cn.chenyang.diytomcat.catalina.Host;
import cn.chenyang.diytomcat.catalina.Service;
import cn.chenyang.diytomcat.utils.MiniBrowser;
import cn.hutool.core.util.StrUtil;
import sun.swing.StringUIClientPropertyKey;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Author: chenyang
 * Time: 2020/8/27
 * Description:
 */
public class Request extends BaseRequest{
    private String requestString;
    private String uri;
    private Socket socket;
    private Context context;
    private Service service;

    public Request(Socket socket, Service service) throws IOException {
        this.socket = socket;
        this.service = service;
        parseRequestString();
        if (StrUtil.isEmpty(requestString))             //请求信息为空直接返回
            return;
        parseUri();
        parseContext();
        if (!"/".equals(context.getPath()))             //将uri去除前缀，因为前缀信息已经存入context中
            uri = StrUtil.removePrefix(uri,context.getPath());
        if (StrUtil.isEmpty(uri))                   //如果uri为空
            uri = "/";
    }

    /*
    初始化该请求的context
     */
    private void parseContext(){
        Engine engine = service.getEngine();
        context = engine.getDefaultHost().getContextMap(uri);           //直接匹配uri
        if (context != null)
            return;

        String path = StrUtil.subBetween(uri,"/","/");      //获取头两个//中的字符串便是文件名
        if (null == path)
            path = "/";
        else
            path = "/"+path;
        context = engine.getDefaultHost().getContextMap(path);                  //根据uri获取的path获取context
        if (null == context)
            context = engine.getDefaultHost().getContextMap("/");                //未获取到则获取根目录
    }

    /*
    获取整个http请求信息
     */
    private void parseRequestString() throws IOException {
        byte[] bytes = MiniBrowser.readBytes(socket.getInputStream(),false);        //只是读取socket的信息，不持续读
        requestString = new String(bytes,StandardCharsets.UTF_8);
    }

    /*
    获取请求uri
     */
    private void parseUri(){
        String temp = StrUtil.subBetween(requestString," "," ");    //获取头两个空格之间的字符串便为uri
        if (!StrUtil.contains(temp,'?')){
            uri = temp;
            return;
        }
        temp = StrUtil.subBefore(temp,'?',false);
        uri = temp;
    }

    public String getRequestString() {
        return requestString;
    }

    public String getUri() {
        return uri;
    }

    public Context getContext() {
        return context;
    }
}

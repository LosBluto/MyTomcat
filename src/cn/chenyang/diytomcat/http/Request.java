package cn.chenyang.diytomcat.http;

import cn.chenyang.diytomcat.utils.MiniBrowser;
import cn.hutool.core.util.StrUtil;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Author: chenyang
 * Time: 2020/8/27
 * Description:
 */
public class Request {
    private String requestString;
    private String uri;
    private Socket socket;

    public Request(Socket socket) throws IOException {
        this.socket = socket;
        parseRequestString();
        if (StrUtil.isEmpty(requestString))             //请求信息为空直接返回
            return;
        parseUri();
    }

    /*
    获取整个http请求信息
     */
    private void parseRequestString() throws IOException {
        byte[] bytes = MiniBrowser.readBytes(socket.getInputStream());
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
}

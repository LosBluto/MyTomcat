package cn.chenyang.diytomcat.utils;

import jdk.internal.util.xml.impl.Input;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Author: chenyang
 * Time: 2020/8/26
 * Description:
 */
public class MiniBrowser {

    public static void main(String[] args){
        String url = "http://static.how2j.cn/diytomcat.html";
        System.out.println(getHttpString(url));
        System.out.println(getContentString(url));
    }


    public static byte[] getContentBytes(String url){
        return getContentBytes(url,false);
    }

    public static String getContentString(String url){
        return getContentString(url,false);
    }

    public static String getContentString(String url,boolean gzip){
        byte[] result = getContentBytes(url, gzip);
        if (null == result){
            return null;
        }
        try {
            return new String(result, StandardCharsets.UTF_8);
        }catch (UnsupportedCharsetException e){
            return null;
        }
    }

    public static byte[] getContentBytes(String url,boolean gzip){      //截取消息体
        byte[] response = getHttpBytes(url, gzip);
        byte[] doubleReturn = "\r\n\r\n".getBytes();

        int pos =  -1;
        for (int i = 0;i<response.length-doubleReturn.length; i++){
            byte[] temp  = Arrays.copyOfRange(response,i,i+doubleReturn.length);

            if (Arrays.equals(temp,doubleReturn)){
                pos = i;
                break;
            }
        }
        if (-1 == pos)
            return null;

        pos += doubleReturn.length;
        return Arrays.copyOfRange(response,pos,response.length);
    }

    public static String getHttpString(String url,boolean gzip){
        byte[] bytes = getHttpBytes(url, gzip);
        return new String(bytes).trim();
    }

    public static String getHttpString(String url){
        return getHttpString(url,false);
    }

    public static byte[] getHttpBytes(String url,boolean gzip){
        byte[] result = null;
        try {
            URL u = new URL(url);
            Socket client = new Socket();
            int port = u.getPort();

            if (-1 == port)             //默认端口??
                port = 80;

            InetSocketAddress inetSocketAddress = new InetSocketAddress(u.getHost(),port);
            client.connect(inetSocketAddress,1000);          //连接该url

            Map<String, String> requestHeaders = new HashMap<>();       //暂时存放部分请求头
            requestHeaders.put("Host",u.getHost()+":"+port);
            requestHeaders.put("Accept","text/html");
            requestHeaders.put("Connection","close");
            requestHeaders.put("User-Agent","how2j mini browser / java1.8");

            if (gzip)
                requestHeaders.put("Accept-Encoding","gizp");

            String path = u.getPath();
            if (path.length() == 0)
                path = "/";

            String firstLine = "GET "+path+" HTTP/1.1\r\n";

            StringBuffer httpRequestString = new StringBuffer();
            httpRequestString.append(firstLine);

            Set<String> headers = requestHeaders.keySet();          //获取map中的键
            for (String header:headers){
                String headLine = header+":"+requestHeaders.get(header)+"\r\n";
                httpRequestString.append(headLine);
            }

            System.out.println(httpRequestString);

            PrintWriter printWriter = new PrintWriter(client.getOutputStream(),true);
            printWriter.println(httpRequestString);
            InputStream is = client.getInputStream();

            result = readBytes(is,true);                //会读取文件，持续读取
            client.close();

        }catch (Exception e){
            e.printStackTrace();
            try {
                result = e.toString().getBytes(StandardCharsets.UTF_8);
            }catch (UnsupportedCharsetException e1){
                e1.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 读取输入流中的数据
     * @param is 输入流
     * @param fully 判断是否需要持续读取，因为传输文件的时候，可能不会以每次1024传输
     * @return 返回二进制
     */
    public static byte[] readBytes(InputStream is,boolean fully) throws IOException {
        int byteSize = 1024;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[byteSize];

        while (true){
            int length = is.read(buffer);           //信息读取到buffer缓存中,并返回一次读取的长度
            if (-1 == length)                       //读取结束
                break;
            baos.write(buffer,0,length);        //把缓存写如输出流中
            if (!fully && length != byteSize)                 //缓存读取长度小于1024则读取完成
                break;
        }
        return baos.toByteArray();
    }
}

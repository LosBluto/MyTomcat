package cn.chenyang.diytomcat;

import cn.hutool.core.util.NetUtil;
import cn.chenyang.diytomcat.http.Request;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Author: chenyang
 * Time: 2020/8/21
 * Description:
 */
public class BootStrap {
    public static void main(String[] args){
        try {
            int port = 18080;
            if (!NetUtil.isUsableLocalPort(port)) {
                System.err.printf("端口%d已占用", port);
                return;
            }
            ServerSocket ss = new ServerSocket(port);
            while (true){
                Socket s = ss.accept();                     //获取套接字
                Request request = new Request(s);
                String requestString = request.getRequestString();
                System.out.println("输出的信息为:\r\n"+requestString);

                OutputStream os = s.getOutputStream();
                String response_head = "HTTP/1.1 200 OK\r\n" + "Content-Type: text/html\r\n\r\n";
                String responseString = response_head+"Hello DIY Tomcat from how2j.cn";
                os.write(responseString.getBytes());
                os.flush();
                os.close();

            }

        }catch (IOException e){
            e.printStackTrace();
        }

    }
}

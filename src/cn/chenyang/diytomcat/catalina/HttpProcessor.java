package cn.chenyang.diytomcat.catalina;

import cn.chenyang.diytomcat.catalina.servlets.DefaultServlet;
import cn.chenyang.diytomcat.catalina.servlets.InvokerServlet;
import cn.chenyang.diytomcat.webappservlet.HelloServlet;
import cn.chenyang.diytomcat.http.Request;
import cn.chenyang.diytomcat.http.Response;
import cn.chenyang.diytomcat.utils.Constant;
import cn.chenyang.diytomcat.utils.WebXmlUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.LogFactory;
import sun.nio.cs.StreamDecoder;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Author: chenyang
 * Time: 2020/10/10
 * Description: 用于发送请求
 */
public class HttpProcessor {
    public void execute(Socket s, Request request,Response response){
        try {
            String uri = request.getUri();                          //获取uri
            if (null == uri)
                return;
            System.out.println(uri);

            Context context = request.getContext();
            String servletClassName = context.getServletClassName(uri);         //从context中获取servlet的位置

            if (null != servletClassName) {                   //可以映射到servlet中,则利用反射机制
                LogFactory.get().info("servletClassName:"+servletClassName);
                InvokerServlet.getInstance().service(request,response);         //处理自定义的servlet
            }else {
                DefaultServlet.getInstance().service(request,response);         //处理默认的servlet
            }

            if (Constant.CODE_200 == response.getStatus()){
                handle200(s,response);
            }
            if (Constant.CODE_404 == response.getStatus()){
                handle404(s,uri);
            }

        }catch (Exception e){
            e.printStackTrace();
            handle500(s,e);
        }finally {
            try {
                if (!s.isClosed())
                    s.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    private static void handle200(Socket s, Response response) throws IOException {
        String contentType = response.getContentType();
        String headText = Constant.response_head_200;
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

    protected  void handle404(Socket s, String uri) throws IOException{
        OutputStream os = s.getOutputStream();
        String responseText = StrUtil.format(Constant.textFormat_404,uri,uri);
        responseText = Constant.response_head_404+responseText;
        byte[] responseByte = responseText.getBytes(StandardCharsets.UTF_8);
        os.write(responseByte);
    }

    protected void handle500(Socket s,Exception e){
        try {
            OutputStream os = s.getOutputStream();

            StackTraceElement[] stes = e.getStackTrace();       //把错误堆栈信息转为string
            StringBuilder sb = new StringBuilder();
            sb.append("\t");
            sb.append(e.toString());
            sb.append("\r\n");
            for (StackTraceElement ste:stes){
                sb.append("\t");
                sb.append(ste.toString());
                sb.append("\r\n");
            }

            String message = e.getMessage();
            if (null != message && message.length()>20){
                message = message.substring(0,19);
            }

            String text = StrUtil.format(Constant.textFormat_500,message,e.toString(),sb.toString());
            text = Constant.response_head_500+text;
            os.write(text.getBytes(StandardCharsets.UTF_8));
            os.close();
        }catch (IOException e1){
            e1.printStackTrace();
        }
    }
}

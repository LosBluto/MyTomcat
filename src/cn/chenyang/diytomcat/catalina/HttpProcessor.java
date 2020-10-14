package cn.chenyang.diytomcat.catalina;

import cn.chenyang.diytomcat.webappservlet.HelloServlet;
import cn.chenyang.diytomcat.http.Request;
import cn.chenyang.diytomcat.http.Response;
import cn.chenyang.diytomcat.utils.Constant;
import cn.chenyang.diytomcat.utils.WebXmlUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

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

            if("/500.html".equals(uri)){                        //自己制作的错误
                throw new Exception("this is a deliberately created exception");
            }

            if ("/hello".equals(uri)){
                HelloServlet helloServlet = new HelloServlet();
                helloServlet.doGet(request,response);
            }else {
                if ("/".equals(uri)) {                                  //无uri
                    //                                String html = "Hello DIY Tomcat from how2j.cn";
                    uri = WebXmlUtil.getWelComeFile(context);
                }                                                   //有uri寻找相应的文件
                String fileName = StrUtil.removePrefix(uri, "/");
                File file = FileUtil.file(context.getDocBase(), fileName);

                if (file.exists()) {
                    String extName = FileUtil.extName(file);        //获取后缀
                    String mimeType = WebXmlUtil.getMimeType(extName);
                    response.setContentType(mimeType);

                    //                                    String fileContent = FileUtil.readUtf8String(file);
                    //                                    response.getPrintWriter().println(fileContent);

                    response.setBody(FileUtil.readBytes(file));                 //直接把file读取为二进制

                    if (fileName.equals("timeConsume.html"))                    //模拟耗时，三秒延迟
                        ThreadUtil.sleep(1000);
                } else
                    handle404(s, uri);
            }
            handle200(s, response);
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

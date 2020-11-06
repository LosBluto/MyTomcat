package cn.chenyang.diytomcat.catalina.servlets;

import cn.chenyang.diytomcat.catalina.Context;
import cn.chenyang.diytomcat.http.Request;
import cn.chenyang.diytomcat.http.Response;
import cn.chenyang.diytomcat.utils.Constant;
import cn.chenyang.diytomcat.utils.WebXmlUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * Author: chenyang
 * Time: 2020/11/5
 * Description:
 */
public class DefaultServlet extends HttpServlet {
    private static DefaultServlet instance = new DefaultServlet();

    public static synchronized DefaultServlet getInstance() {
        return instance;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Request request = (Request) req;
        Response response = (Response) resp;

        Context context = request.getContext();

        String uri = request.getUri();

        if ("/500.html".equals(uri))                        //自己制作的错误
            throw new RuntimeException("this is a deliberately created exception");

        if ("/".equals(uri)) {                                  //无uri
            uri = WebXmlUtil.getWelComeFile(context);
        }                                                   //有uri寻找相应的文件
        String fileName = StrUtil.removePrefix(uri, "/");
        File file = FileUtil.file(context.getDocBase(), fileName);

        if (file.exists()) {
            String extName = FileUtil.extName(file);        //获取后缀
            String mimeType = WebXmlUtil.getMimeType(extName);
            response.setContentType(mimeType);

            response.setBody(FileUtil.readBytes(file));                 //直接把file读取为二进制

            if (fileName.equals("timeConsume.html"))                    //模拟耗时，三秒延迟
                ThreadUtil.sleep(1000);
            response.setStatus(Constant.CODE_200);
        } else {
            response.setStatus(Constant.CODE_404);
        }
    }
}

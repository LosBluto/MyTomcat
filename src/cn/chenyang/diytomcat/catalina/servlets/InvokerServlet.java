package cn.chenyang.diytomcat.catalina.servlets;

import cn.chenyang.diytomcat.catalina.Context;
import cn.chenyang.diytomcat.http.Request;
import cn.chenyang.diytomcat.http.Response;
import cn.chenyang.diytomcat.utils.Constant;
import cn.hutool.core.util.ReflectUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import java.io.IOException;

/**
 * Author: chenyang
 * Time: 2020/10/18
 * Description: 负责处理自定义的servlet
 */
public class InvokerServlet extends HttpServlet {
    private static InvokerServlet instance = new InvokerServlet();

    public static synchronized InvokerServlet getInstance(){
        return instance;
    }
    //无法初始化该变量，一个服务器只有一个invoker
    private InvokerServlet(){

    }

    /*
    利用反射机制调用servlet
     */
    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {     //利用反射调用指定servlet
        Request request = (Request) req;
        Response response = (Response) res;

        String uri = request.getUri();
        Context context = request.getContext();
        String servletClassName= context.getServletClassName(uri);

        try {
            Class<?> servletClass = context.getWebappClassLoader().loadClass(servletClassName);     //使用classloader加载出class
            System.out.println("servletClass:" + servletClass);
            System.out.println("servletClass'classLoader:" + servletClass.getClassLoader());

            Object servletObject = ReflectUtil.newInstance(servletClass);                   //把class实例化
            ReflectUtil.invoke(servletObject, "service", request, response);
            response.setStatus(Constant.CODE_200);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}

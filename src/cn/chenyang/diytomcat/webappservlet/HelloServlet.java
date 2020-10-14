package cn.chenyang.diytomcat.webappservlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: chenyang
 * Time: 2020/10/10
 * Description:
 */
public class HelloServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            resp.getWriter().println("Hello DIY Tomcat from HelloServlet");
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

package cn.chenyang.diytomcat.test;

import cn.chenyang.diytomcat.MiniBrowser;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Author: chenyang
 * Time: 2020/8/26
 * Description:
 */
public class TestTomcat {
    private static int port = 18080;
    private static String host = "127.0.0.1";

    @BeforeClass
    public static void beforeClass(){
        if (NetUtil.isUsableLocalPort(port)){
            System.err.printf("请启动端口号:%d",port);
            System.exit(1);
        }else {
            System.out.println("服务器已启动，开始测试");
        }
    }

    private String getContentString(String uri){
        String url = StrUtil.format("http://{}:{}{}",host,port,uri);
        return MiniBrowser.getContentString(url);
    }

    private String getHttpString(String uri){
        String url = StrUtil.format("http://{}:{}{}",host,port,uri);
        return MiniBrowser.getHttpString(url);
    }

    @Test
    public void testHello(){
        String html = getContentString("/?name=gareen ");
        Assert.assertEquals(html,"Hello DIY Tomcat from how2j.cn");
        System.out.println(html);
    }
}

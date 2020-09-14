package cn.chenyang.diytomcat.test;

import cn.chenyang.diytomcat.utils.MiniBrowser;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.StrUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import sun.plugin2.util.SystemUtil;

import java.util.Date;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
        String html = getContentString("/");
        System.out.println("html:"+html);
        Assert.assertEquals(html,"Hello DIY Tomcat from how2j.cn\n");

    }

    /*
    测试文件的访问
     */
    @Test
    public void testHtml(){
        String html = getContentString("/a.html");
        Assert.assertEquals(html,"Hello DIY Tomcat from a.html\n");
    }

    /*
    测试耗时任务
     */
    @Test
    public void testTimeConsumeHtml() throws InterruptedException {
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20,30,60, TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(10));             //创建线程池

        TimeInterval timeInterval = DateUtil.timer();                   //计时器开始计时

        for (int i = 0;i<3; i++){               //启用三个线程
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    getContentString("/timeConsume.html");
                }
            });
        }
        threadPool.shutdown();                  //关闭线程池，不会开启新的线程
        threadPool.awaitTermination(1,TimeUnit.HOURS); //最长, 等待一小时

        long duration = timeInterval.intervalMs();              //结束计时
        System.out.println("duration:"+duration);
        Assert.assertTrue(duration < 3000);
    }


}

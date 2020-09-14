package cn.chenyang.diytomcat.utils;

import org.junit.runner.Runner;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author: chenyang
 * Time: 2020/9/11
 * Description:
 */
public class ThreadPoolUtil {
    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(20,20,60, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(10));

    public static void run(Runnable r){
        threadPool.execute(r);
    }
}

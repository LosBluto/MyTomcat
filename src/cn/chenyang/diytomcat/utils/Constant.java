package cn.chenyang.diytomcat.utils;

import cn.hutool.system.SystemUtil;

import java.io.File;

/**
 * Author: chenyang
 * Time: 2020/8/30
 * Description:
 */
public class Constant {
    public static final String response_head_202 =
            "HTTP/1.1 200 OK\r\n"+
                    "Content-Type: {}\r\n\r\n";


    public static final File webappsFolder = new File(SystemUtil.get("user.dir"),"webapps");        //设置资源目录
    public static final File rootFolder = new File(webappsFolder,"ROOT");
}

package cn.chenyang.diytomcat.catalina;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;

/**
 * Author: chenyang
 * Time: 2020/9/14
 * Description: 用于确认应用的路径
 */
public class Context {
    private String path;
    private String docBase;

    public Context(String path, String docBase) {
        TimeInterval timeInterval = DateUtil.timer();           //计时便于输出日志
        this.path = path;
        this.docBase = docBase;
        LogFactory.get().info("Deploying web application directory {}", this.docBase);
        LogFactory.get().info("Deployment of web application directory {} has finished in {} ms",
                timeInterval.intervalMs());
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDocBase(String docBase) {
        this.docBase = docBase;
    }

    public String getPath() {
        return path;
    }

    public String getDocBase() {
        return docBase;
    }
}

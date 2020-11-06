package cn.chenyang.diytomcat.catalina;

import cn.chenyang.diytomcat.utils.ServerXmlUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.log.LogFactory;

import java.util.List;

/**
 * Author: chenyang
 * Time: 2020/9/23
 * Description:
 */
public class Service {
    private Server server;
    private String name;

    private Engine engine;
    private List<Connector> connectors;

    public Service(Server server) {
        this.server = server;
        this.name = ServerXmlUtil.getServiceName();
        this.engine = new Engine(this);
        this.connectors = ServerXmlUtil.getConnectors(this);            //直接从xml中获取
    }

    public Server getServer() {
        return server;
    }

    public Engine getEngine() {
        return engine;
    }

    public void start(){
        init();
    }

    private void init(){
        TimeInterval timeInterval = DateUtil.timer();               //开启计时器
        for (Connector c: connectors)
            c.init();
        LogFactory.get().info("Initialization processed in {} ms",timeInterval.intervalMs());//运行时间
        for (Connector c: connectors)   //开启连接器
            c.start();
    }
}

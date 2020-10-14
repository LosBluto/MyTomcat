package cn.chenyang.diytomcat.catalina;

import cn.chenyang.diytomcat.utils.ServerXmlUtil;

import java.util.List;

/**
 * Author: chenyang
 * Time: 2020/9/16
 * Description: 管理多个host
 */
public class Engine {
    private String defaultHost;
    private Service service;
    private List<Host> hosts;

    public Engine(Service service) {
        this.defaultHost = ServerXmlUtil.getEngineDefaultHost();
        this.service = service;
        this.hosts = ServerXmlUtil.getHosts(this);
        cheackDefault();
    }

    public Service getService() {
        return service;
    }

    private void cheackDefault(){
        if (null == getDefaultHost())
            throw new RuntimeException("the defaultHost" + defaultHost + " does not exist!");
    }

    public Host getDefaultHost(){
        for (Host host:hosts){
            if (host.getName().equals(defaultHost))
                return host;
        }
        return null;
    }
}

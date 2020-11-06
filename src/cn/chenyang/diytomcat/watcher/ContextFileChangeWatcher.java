package cn.chenyang.diytomcat.watcher;

import cn.chenyang.diytomcat.catalina.Context;
import cn.hutool.core.io.watch.WatchMonitor;
import cn.hutool.core.io.watch.WatchUtil;
import cn.hutool.core.io.watch.Watcher;
import cn.hutool.log.LogFactory;

import java.nio.file.Path;
import java.nio.file.WatchEvent;

/**
 * Author: chenyang
 * Time: 2020/11/6
 * Description:
 */
public class ContextFileChangeWatcher {
    private WatchMonitor watchMonitor;              //真正起作用的监听器
    private boolean stop = false;                   //是否暂停监听

    public ContextFileChangeWatcher(Context context) {
        this.watchMonitor = WatchUtil.createAll(context.getDocBase(), Integer.MAX_VALUE, new Watcher() {

            private void dealWith(WatchEvent<?> event){
                synchronized (ContextFileChangeWatcher.class){          //使用同步，让改变变成同步的，防止更新覆盖
                    String fileName = event.context().toString();
                    if (stop)               //若暂停监听
                        return;
                    if (fileName.endsWith(".jar") || fileName.endsWith(".class") || fileName.endsWith(".xml")){
                        stop = true;                    //已经扫描到改变则暂停监听，防止重复加载
                        LogFactory.get().info(ContextFileChangeWatcher.this+"检测到web应用下的重要文件变化{}",fileName);
                        context.reload();
                    }
                }
            }

            @Override
            public void onCreate(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent);
            }

            @Override
            public void onModify(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent);
            }

            @Override
            public void onDelete(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent);
            }

            @Override
            public void onOverflow(WatchEvent<?> watchEvent, Path path) {
                dealWith(watchEvent);
            }
        });
    }

    public void start(){
        watchMonitor.start();
    }

    public void stop(){
        watchMonitor.close();
    }
}

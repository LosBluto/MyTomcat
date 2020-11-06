package cn.chenyang.diytomcat;

import cn.chenyang.diytomcat.utils.ThreadPoolUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Author: chenyang
 * Time: 2020/10/30
 * Description:
 */
public class server {
    public static void main(String[] args){
        try {

            ServerSocket ss = new ServerSocket(80);
            while (true){
                Socket s = ss.accept();
                Runnable runnable = new Runnable() {        //创建新任务
                    @Override
                    public void run() {
                        byte[] result = "hello jcy".getBytes();
                        try {
                            OutputStream os = s.getOutputStream();
                            os.write(result);
                            os.flush();
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ThreadPoolUtil.run(runnable);               //放入线程池中启动任务
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

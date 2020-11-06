package cn.chenyang.diytomcat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Author: chenyang
 * Time: 2020/10/30
 * Description:
 */
public class client {
    public static void main(String[] args){
        try {
            Socket client = new Socket();
            InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 80);
            client.connect(inetSocketAddress, 1000);

            InputStream is = client.getInputStream();
            byte[] result = readBytes(is);
            System.out.println("接收信息为: "+ new String(result).trim());

        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static byte[] readBytes(InputStream is) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();       //存放结果

        while (true){
            int length = is.read(buffer);
            if (-1 == length)                           //未读取处也表明读取完毕
                break;

            baos.write(buffer,0,length);

            if (length != 1024)                         //长度不足1024则读取完毕
                break;
        }

        return baos.toByteArray();
    }
}

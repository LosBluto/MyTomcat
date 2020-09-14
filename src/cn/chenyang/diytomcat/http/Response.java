package cn.chenyang.diytomcat.http;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * Author: chenyang
 * Time: 2020/8/30
 * Description:
 */
public class Response {
    private StringWriter stringWriter;
    private PrintWriter printWriter;
    private String contentType;

    public Response(){
        this.stringWriter = new StringWriter();
        this.printWriter = new PrintWriter(stringWriter);
        this.contentType = "text/html";
    }

    public String getContentType() {
        return contentType;
    }

    public PrintWriter getPrintWriter() {
        return printWriter;
    }

    public byte[] getBody() throws UnsupportedEncodingException{
        String content = stringWriter.toString();
        return content.getBytes(StandardCharsets.UTF_8);
    }

}

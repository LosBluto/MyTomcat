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
public class Response extends BaseResponse{
    private StringWriter stringWriter;
    private PrintWriter printWriter;
    private byte[] body;
    private String contentType;
    private int status;

    public Response(){
        this.stringWriter = new StringWriter();
        this.printWriter = new PrintWriter(stringWriter);
        this.contentType = "text/html";
    }

    @Override
    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public int getStatus() {
        return status;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public PrintWriter getWriter() {
        return printWriter;
    }

    public byte[] getBody() throws UnsupportedEncodingException{
        if (body == null) {
            String content = stringWriter.toString();
            return content.getBytes(StandardCharsets.UTF_8);
        }
        return body;
    }

}

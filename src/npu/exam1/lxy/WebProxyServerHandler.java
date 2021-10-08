package npu.exam1.lxy;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Date;
import java.util.Locale;
import java.util.Stack;
//PUT src/ssd8/socket/http/Client.java HTTP/1.1

/**
 * work thread
 *
 * @author wben
 */
/*
 * 解析命令
 * 响应封装
 * 转发
 * 拿到外网的封装*/
public class WebProxyServerHandler implements Runnable {
    private Socket socket;
    private File rootDirectory;
    private static String CRLF = "\r\n";
    OutputStream outputStream;
    Writer out;
    BufferedInputStream in;
    public static final String SAVE_DIR = "E:\\网络分布与计算\\实验内容\\Exercise2\\";
    private WebProxy webProxy;

    public WebProxyServerHandler(Socket socket, File rootDir) throws IOException {
        this.socket = socket;
        webProxy = new WebProxy();
        //检查根目录是否合格
        if (rootDir.isFile()) {
            throw new IOException("根目录必须为文件夹");
        }
        try {
            rootDirectory = rootDir.getCanonicalFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 初始化输入输出
     * @throws IOException
     */
    void init() throws IOException {
        outputStream = new BufferedOutputStream(socket.getOutputStream());
        out = new OutputStreamWriter(outputStream);
        in = new BufferedInputStream(socket.getInputStream());
    }


    public void run() {
        try {
            System.out.println("新连接，连接地址：" + socket.getInetAddress() + "："
                    + socket.getPort());
            init();
            //调用proxy处理
            ProxyProcess();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (null != socket) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * proxy处理过程：转发->回复
     * @throws Exception
     */
    void ProxyProcess() throws Exception {
        try {
            StringBuilder requestLine = new StringBuilder();
            int ch = 0;
            while ((ch = in.read()) != -1) {
                requestLine.append((char) ch);
                if (ch == '\n') break;
            }


            String[] reqs = requestLine.toString().split(" ");
            //reqs1->put/get reqs2->url reqs3->http version

            //读入ori get请求，分析路径，端口号，重新用httpclient 发送请求
            URL resendURL = new URL(reqs[1]);
            webProxy.connect(resendURL.getHost(), resendURL.getPort() == -1 ? 80 : resendURL.getPort());
            String resendRequest = "GET " + resendURL.getFile() + " HTTP/1.0";
            webProxy.processGetRequest(resendRequest);


            //接受请求
            String header = webProxy.getHeader() + CRLF;
            String response = webProxy.getResponse();

            //请求传给服务器
            out.write(header);
            out.flush();
            outputStream.write(response.getBytes("iso-8859-1"));
            out.flush();
            out.close();
        } catch (Exception e) {
            //如果服务器端出错将返回500 Internal ProxyServer Error
            String ErrorResponce = getResponseBody("HTTP/1.1 500 Internal ProxyServer Error", e.toString());
            out.write(getResponseHeader("HTTP/1.1 500 Internal ProxyServer Error", "text/html; charset=utf-8", ErrorResponce.length()));
            out.flush();
            out.write(ErrorResponce);
            out.flush();
            out.close();
        }
    }



    private String getResponseBody(String title, String body) {
        String htmlStr = new StringBuilder("<HTML>\r\n").append("<HEAD>")
                .append(title).append("</TITLE>\r\n").append("</HEAD>\r\n")
                .append("<BODY>").append("<H1>").append(body)
                .append("</H1>\r\n").append("</BODY></HTML>\r\n").toString();
        return htmlStr;
    }

    private String getResponseHeader(String code, String mimi, int length) {
        StringBuilder headers = new StringBuilder().
                append(code + CRLF).
                append("Server: MyServer/1.0" + CRLF).
                append("Content-Type:" + mimi + CRLF).
                append("ProxyServer:"+"proxyServer/1.0"+CRLF).
                append("Content-Length: " + length + CRLF).
                append("Date:" + (new Date()) + CRLF + CRLF);
        return headers.toString();
    }


}



## **Exam 1 WEB代理服务器**

#### 实验思路

1. 接收客户端发来的get请求
2. 解析get指令中的URL，获取目标服务器地址和目标服务器文件
3. 向目标服务器转发GET请求
4. 从目标服务器端接收响应消息
5. 将响应消息发送给客户端

#### 主要代码实现

- 接收client端的请求，并解析url，向目标服务器发送请求

```java
URL resendURL = new URL(reqs[1]);
webProxy.connect(resendURL.getHost(), resendURL.getPort() == -1 ? 80 : resendURL.getPort());
String resendRequest = "GET " + resendURL.getFile() + " HTTP/1.0";
webProxy.processGetRequest(resendRequest);
```

- 接收服务器返回的响应头和响应体，将响应发送给client

```java
String header = webProxy.getHeader() + CRLF;
String response = webProxy.getResponse();
  
out.write(header);
out.flush();
outputStream.write(response.getBytes("iso-8859-1"));
out.flush();
out.close();

```

- 代理服务器异常处理500 Internal ProxyServer Error

```java
String ErrorResponce = getResponseBody("HTTP/1.1 500 Internal ProxyServer Error", e.toString());
out.write(getResponseHeader("HTTP/1.1 500 Internal ProxyServer Error", "text/html; charset=utf-8", ErrorResponce.length()));
out.flush();
out.write(ErrorResponce);
out.flush();
out.close();
```


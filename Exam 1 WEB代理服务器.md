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

#### 实验结果

本实验结果使用实验2的client.java进行测试：通过client访问实验2中的占用80端口的服务器。

开启80端口服务器和8000端口的代理服务器，开启客户端和代理服务器相连接

![image-20211008170805775](C:\Users\lxy\AppData\Roaming\Typora\typora-user-images\image-20211008170805775.png)

使用指令`GET http://127.0.0.1:80/face.jpg HTTP/1.1`

通过代理服务器获得端口为80的服务器下的`face.jpg`

结果如下

![image-20211008171050719](C:\Users\lxy\AppData\Roaming\Typora\typora-user-images\image-20211008171050719.png)

获得`face.jpg`如下

![image-20211008171131642](C:\Users\lxy\AppData\Roaming\Typora\typora-user-images\image-20211008171131642.png)





通过浏览器访问

![image-20211008173602554](C:\Users\lxy\AppData\Roaming\Typora\typora-user-images\image-20211008173602554.png)

访问网址`http://211.143.237.227:9999/`

![image-20211008173641577](C:\Users\lxy\AppData\Roaming\Typora\typora-user-images\image-20211008173641577.png)

访问成功
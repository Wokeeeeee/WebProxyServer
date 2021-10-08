package npu.exam1.lxy;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 多线程启动
 */
public class WebProxyServerThreadPool {
    ExecutorService executorService; // 线程池
    final int POOL_SIZE = 6; // 单个处理器线程池工作线程数目
    ServerSocket serverSocket;
    private final int PORT = 8000;
    private final File rootDir;

    /**
     * 接收启动根目录，以端口8000启动
     * @param rootDir
     * @throws IOException
     */
    public WebProxyServerThreadPool(File rootDir) throws IOException {
        if (!rootDir.isDirectory()) {
            throw new IOException(rootDir + "不是目录");
        }
        serverSocket = new ServerSocket(PORT);
        this.rootDir = rootDir;
        //线程池创建 定长线程池
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime()
                .availableProcessors() * POOL_SIZE);
        System.out.println("服务器启动。端口号8000。");
    }


    /**
     * 多线程proxyserver启动
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        File root;
        System.out.println("根目录参数：>" + args[0]);
        root = new File(args[0]);
        new WebProxyServerThreadPool(root).servic();
    }

    /**
     * service implements
     */
    public void servic() {
        while (true) {
            Socket socket = null;
            try {
                socket = serverSocket.accept();
                Thread work = new Thread(new WebProxyServerHandler(socket, this.rootDir));

                work.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

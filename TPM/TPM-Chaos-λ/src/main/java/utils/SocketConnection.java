package utils;

/**
 * @author : wangxuan
 * @date : 10:04 2020/12/10 0010
 */
import tree_parity_machine.TreeParityMachine;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketConnection {

    private int port;
    public String host;
    private final String ENCODING = "UTF-8";
    private ServerSocket serverSocket;
    private Socket socket;
    private WorkClass workClass;
    public String ip;

    public SocketConnection(int port, String host){
        this.port = port;
        this.host = host;
    }


    public String listen(String content, SocketFunction function) throws IOException {
        //单例双重验证
        if(serverSocket==null){
            synchronized (this){
                if(serverSocket==null)
                    serverSocket = createServer(port);
            }
        }

        try {
            if(socket == null)
                socket = createServerSocket(serverSocket);
            //通常获取到socket连接后，都是分配一个线程来处理客户端
            if(workClass==null)
                workClass = new WorkClass(socket, content, function);
            workClass.setFunction(function);
            workClass.setContent(content);
            String msg = workClass.work_server();
            return msg;
        }catch(IOException ioe){
            ioe.printStackTrace();
            throw ioe;
        }finally{
        }
    }

    class WorkClass{
        private Socket socket ;
        private String content;
        private SocketFunction function;

        public void setContent(String content) {
            this.content = content;
        }

        public void setFunction(SocketFunction function) {
            this.function = function;
        }

        public WorkClass(Socket socket, String content, SocketFunction function){
            this.socket = socket;
            this.content = content;
            this.function = function;
        }

        public String work_server() throws IOException {
            String msg = null;
            InputStream in = null;
            OutputStream out = null;
            try {

                in = socket.getInputStream();
                msg = input(in);

                out = socket.getOutputStream();
                output(out, content);

                //通知客户端已经发送完
                //socket.shutdownOutput();

                function.callback(msg);
                return msg;
            }finally{
                //关闭输入输出流
                //streamClose(in, out);
            }
        }
        public String work_client() throws IOException {
            String msg = null;
            InputStream in = null;
            OutputStream out = null;
            try {

                out = socket.getOutputStream();
                output(out, content);

                //通知服务器端已经发送完成
                //socket.shutdownOutput();


                in = socket.getInputStream();
                msg = input(in);
                function.callback(msg);
                return msg;
            }finally{
                //关闭输入输出流
                //streamClose(in, out);
            }
        }
    }

    public String send(String content, SocketFunction function) throws IOException {
        String msg = null;
        //单例双重验证
        if(socket==null){
            synchronized (this){
                if(socket==null)
                    socket = createClientSocket(host, port);
            }
        }
        try {
            if(workClass == null)
                workClass = new WorkClass(socket, content, function);
            workClass.setFunction(function);
            workClass.setContent(content);
            msg = workClass.work_client();
            return msg;
        }catch(UnknownHostException uhe){
            uhe.printStackTrace();
            throw new IOException("主机连接创建异常：" + uhe.getMessage());
        }catch(IOException ioe){
            ioe.printStackTrace();
            throw ioe;
        }
    }

    public void streamClose(InputStream in, OutputStream out){
        //IOUtils.closeQuietly(in); 可用IOUtils工具类关闭流
        if (in != null){
            try {
                in.close();
            }catch(IOException ioe){
                System.out.println("关闭输入流异常：" + ioe.getMessage());
            }
        }
        if (out != null){
            try {
                out.flush();
                out.close();
            }catch(IOException ioe){
                System.out.println("关闭输出流异常：" + ioe.getMessage());
            }
        }
    }

    private ServerSocket createServer(int port) throws IOException {
        System.out.println("监听端口号：" + port);
        return new ServerSocket(port);
    }

    private Socket createServerSocket(ServerSocket serverSocket) throws IOException {
        Socket socket = serverSocket.accept();
        ip = socket.getInetAddress().getHostAddress();
        System.out.println("获取到客户端连接：" + ip);
        return socket;
    }

    private static Socket createClientSocket(String host, int port) throws UnknownHostException, IOException {
        return new Socket(host, port);
    }

    public void serverClose() {
        if ( serverSocket!= null && !serverSocket.isClosed()){
            try{
                serverSocket.close();
            }catch(IOException ioe){
                System.out.println("服务关闭异常：" + ioe.getMessage());
            }
        }
    }

    public void clientClose(){
        if (socket != null && !socket.isClosed()){
            try{
                socket.close();
            }catch(IOException ioe){
                System.out.println("Socket关闭异常：" + ioe.getMessage());
            }
        }
    }

    public OutputStream output(OutputStream out, String content) throws IOException {
        try{
            out.write(content.getBytes(ENCODING));
        }finally{
            return out;
        }
    }

    public OutputStream outputbyte(OutputStream out, byte[] content) throws IOException {
        try{
            out.write(content);
        }finally{
            return out;
        }
    }

    public String input(InputStream in) throws IOException {
        int len;
        char[] b = new char[9000];
        StringBuilder sb = new StringBuilder();
        BufferedReader reader ;
        try {
            //以字符流为主，如需字节流，则不需要BufferedReader和InputStreamReader，可以直接从InputStream中获取或采用对应缓冲包装类
            reader = new BufferedReader(new InputStreamReader(in, ENCODING));
            while ((len = reader.read(b)) != -1) {
                if(len==b.length){
                    sb.append(b, 0, len);
                    continue;
                }
                if(b[len-1]=='\0') {
                    len--;
                    sb.append(b, 0, len);
                    break;
                }
                sb.append(b, 0, len);
            }
            //reader.close();
        }finally{
        }
        return sb.toString();
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public interface SocketFunction{
        void callback(String msg);
    }
}
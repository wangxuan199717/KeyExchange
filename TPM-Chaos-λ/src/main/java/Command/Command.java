package Command;

import utils.AESUtils;
import utils.SensorUtils;
import utils.SocketConnection;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @author : wangxuan
 * @date : 15:00 2020/12/21 0021
 */

/*
* 这个类用于服务器接收命令行，用于执行
* */
public class Command {
    //保存当前已经协商好的秘钥和对应ip地址，：ip：key
    private String default_key;
    private Map<String,String> keys = new HashMap<>();

    public synchronized boolean addkey(String ip,String key){
        if(keys.containsKey(ip))
            return false;
        keys.put(ip,key);
        default_key = key;
        return true;
    }

    public void loop() throws Exception {
        SocketConnection socketConnection = new SocketConnection(8089, "127.0.0.1");
        String message = "ok recivied !";
        while (true) {
            String recivied = socketConnection.listen(AESUtils.encryptData(default_key, message) + "\0", msg -> {
                return;
            });
            System.out.println("接收到原文：" + recivied);
            recivied = AESUtils.decryptData(keys.get(socketConnection.ip), recivied);
            System.out.println("解密后原文： " + recivied);

            //文件传输
            if (recivied.contains("capture")) {
                try {
                    InputStream is = new FileInputStream(recivied.split(" ")[1]);
                    int iAvail = is.available();
                    byte[] bytes = new byte[iAvail];
                    is.read(bytes);
                    message = new String(bytes, "ISO-8859-1");
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }
            //温度
            if (recivied.contains("temp")) {
                String path = "/sys/bus/w1/devices"; // 驱动文件路径
                File f = new File(path);
                if (f.exists()) {
                    File fa[] = f.listFiles();
                    for (int i = 0; i < fa.length; i++) {
                        File fs = fa[i];
                        if (fs.isDirectory() && fs.getName().contains("28-")) {
                            byte[] temp = new byte[100];
                            path = fs.getAbsolutePath();
                            File file = new File(path+"/w1_slave");
                            FileInputStream fileInputStream = new FileInputStream(file);
                            fileInputStream.read(temp);
                            message = new String(temp);
                        }
                    }
                }
                continue;
            }
            //湿度
            if (recivied.contains("dht11")) {
                message = SensorUtils.getDate("dht11");
                continue;
            }
            //天气
            if (recivied.contains("weather")) {
                message = SensorUtils.getDate("weather");
                continue;
            }
            //拍照
            if (recivied.contains("camera")) {
                message = SensorUtils.getDate("camera");
                try {
                    InputStream is = new FileInputStream("test.jpg");
                    int iAvail = is.available();
                    byte[] bytes = new byte[iAvail];
                    is.read(bytes);
                    message = new String(bytes, "ISO-8859-1");
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                continue;
            }
            message="ok recivied !";
            if (recivied.contains("close"))
                break;
        }
        socketConnection.serverClose();
    }

    public void loop_client(String ip,int port) throws Exception {
        SocketConnection socketConnection = new SocketConnection(port,ip);
        while(true) {
            Scanner scanner = new Scanner(System.in);
            String text = scanner.nextLine();
            String message = socketConnection.send(AESUtils.encryptData(keys.get(socketConnection.host),text)+"\0", msg -> {
                return;
            });
            System.out.println("接收到原文："+message);
            message = AESUtils.decryptData(keys.get(socketConnection.host),message);
            System.out.println("解密后原文： "+message);
            if(text.contains("close"))
                break;
            if(text.contains("capture")){
                scanner.nextLine();
                message = socketConnection.send(AESUtils.encryptData(keys.get(socketConnection.host),"已接收到文件")+"\0", msg -> {
                    return;
                });
                System.out.println("接收到原文："+message);
                String content = AESUtils.decryptData(keys.get(socketConnection.host),message);
                //写入文件
                FileOutputStream fos = new FileOutputStream("rec-"+text.split(" ")[1]);
                fos.write(content.getBytes("ISO-8859-1"));
                fos.close();

                System.out.println("解密后原文： "+content);
            }
            if(text.contains("temp")){
                scanner.nextLine();
                message = socketConnection.send(AESUtils.encryptData(keys.get(socketConnection.host),"已接收到温度数据")+"\0", msg -> {
                    return;
                });
                System.out.println("接收到原文："+message);
                String content = AESUtils.decryptData(keys.get(socketConnection.host),message);
                //写入文件
                System.out.println("解密后原文： "+content);

                String temp = content.split("t=")[1];
                temp = temp.split("\n")[0];
                try {
                    System.out.println("当前温度℃：" + (double)Integer.parseInt(temp) / 1000);
                }catch (NumberFormatException n){
                    n.printStackTrace();
                }
            }
            if(text.contains("weather")){
                scanner.nextLine();
                message = socketConnection.send(AESUtils.encryptData(keys.get(socketConnection.host),"已接收到天气数据")+"\0", msg -> {
                    return;
                });
                System.out.println("接收到原文："+message);
                String content = AESUtils.decryptData(keys.get(socketConnection.host),message);
                //写入文件
                System.out.println("解密后原文： "+content);
            }

            if(text.contains("camera")){
                scanner.nextLine();
                message = socketConnection.send(AESUtils.encryptData(keys.get(socketConnection.host),"已接收到相机数据")+"\0", msg -> {
                    return;
                });
                System.out.println("接收到原文："+message);
                String content = AESUtils.decryptData(keys.get(socketConnection.host),message);
                //写入文件
                FileOutputStream fos = new FileOutputStream("rec-camera.jpg");
                fos.write(content.getBytes("ISO-8859-1"));
                fos.close();

                System.out.println("解密后原文： "+content);
            }
        }
    }

    private int parserCommand(String command,String info){
        switch (command){
            case "capture":{
                capture();
                return 1;
            }
            case "write":{
                write(info);
                return 2;
            }
            default:return -1;
        }
    }
    private void capture(){

    }
    private void write(String info){
        System.out.println(info);
    }
}

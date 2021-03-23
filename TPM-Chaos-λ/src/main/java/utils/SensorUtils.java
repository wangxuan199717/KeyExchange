package utils;

/**
 * @author : wangxuan
 * @date : 15:30 2020/12/31 0031
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SensorUtils {

    public static String getDate(String sensor) {
        Process proc;
        String cmd ;
        StringBuilder date = new StringBuilder();
        try {
            switch (sensor){
                case "temp" : {
                    cmd = "sudo ./ds18b20";
                    break;
                }
                case "dht11":{
                    cmd = "sudo ./dht11";
                    break;
                }
                case "weather":{
                    cmd = "sudo ./drop";
                    break;
                }
                case "camera":{
                    cmd = "raspistill -t 100 -o test.jpg";
                    break;
                }
                default:
                    return "error !";
            }
            proc = Runtime.getRuntime().exec(cmd);// 执行
            //用输入输出流来截取结果
            String line = null;
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                date.append(line);

            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return date.toString();
    }
}




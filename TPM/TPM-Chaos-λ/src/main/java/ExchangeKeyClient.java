import Chaos.Logistical;
import Command.Command;
import learning_algorithm.LearningParadigm;
import learning_algorithm.TPMTrainer;
import tree_parity_machine.TreeParityMachine;
import utils.*;
import Chaos.LFSR.GateType;
import Chaos.LFSR.LFSR;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import static learning_algorithm.LearningParadigm.HEBBIAN;

/**
 * @author : wangxuan
 * @date : 16:57 2020/12/10 0010
 */
public class ExchangeKeyClient extends SensorUtils {

    //树型奇偶机参数
    private static int n=20;
    private static int k=3;
    private static int bound=3;
    TreeParityMachine tpm;
    private static LearningParadigm method = HEBBIAN;
    private static TPMTrainer trainer = new TPMTrainer();
    private Command command = new Command();

    //协商后的密钥
    private String key;

    //socket相关
    private int port;
    private String host;
    private SocketConnection socketConnection;

    public boolean createSocket(int port, String host){
        this.port = port;
        this.host = host;
        socketConnection = new SocketConnection(port,host);
        return true;
    }
    public String generateKey_Server() throws IOException {
        String msg1 = socketConnection.listen(n+","+k+","+bound, msg -> {
            System.out.println("协商后的参数: "+msg);
        });
        socketConnection.serverClose();
        tpm = new TreeParityMachine(n, k,-bound, bound, method);
        //生成随机序列
        LFSR lfsr = new LFSR ();
        lfsr.setNumberOfBits ( n*k );
        lfsr.setGateType (GateType.XOR);

        for(int i=0;i<100;i++)
            lfsr.strobeClock (); // Move the LFSR forward one.
        short[] input = new short[n*k];
        String bits = lfsr.getBitsForward();
        for(int i=0;i<n*k;i++){
            input[i] = (short) (bits.charAt(i)==0?-1:1);
        }
        //循环训练，成功则调出循环
        while (true) {
            short out1 = tpm.getOutput(input);
            socketConnection = new SocketConnection(port,host);
            msg1 = socketConnection.listen(out1+","+short2string(tpm.getSecretKey()),msg -> {
                System.out.println("接收到的数据："+msg);
            });
            socketConnection.serverClose();
            int out = msg1.charAt(0);
            msg1 = msg1.substring(2,msg1.length());

            if(out == out1)
                tpm.train(input,out1);
            if(msg1.equals(short2string(tpm.getSecretKey())))
                break;

            //生成随机序列
            for(int i=0;i<100;i++)
                lfsr.strobeClock ();
            input = new short[n*k];
            bits = lfsr.getBitsForward();
            for(int i=0;i<n*k;i++){
                input[i] = (short) (bits.charAt(i)==0?-1:1);
            }
        }
        key = KeyGeneration.generateKey(tpm.getSecretKey(),bound);
        return key;
    }
    public String generateKey_Client() throws Exception {
        queue.clear();
        obtainParameter.getList_window().clear();
        false_step = 0;
        true_step = 0;

        String msg1 = socketConnection.send(n+","+k+","+bound+'\0', msg -> {
            System.out.println("协商后的参数: "+n+","+k+","+bound);
        });
        tpm = new TreeParityMachine(n, k,-bound, bound, method);

        short[] input;

        //协商的初始值
        double u=3.98;
        double x=0.5;
        int N=100;

        //双方的输入序列产生器
        Logistical logistical = new Logistical(u,x);
        logistical.GetSqueue(N,-1,1);
        input = logistical.GetSqueue(n*k,-1,1);

        //循环训练，成功则调出循环
        while (true) {
            short out1 = tpm.getOutput(input);
            //socketConnection = new SocketConnection(port,host);
            msg1 = socketConnection.send(out1+","+short2string(tpm.getSecretKey())+'\0',msg -> {
                //System.out.println("接收到的数据："+msg);
            });
            int out = msg1.charAt(0)=='-'?-1:1;
            msg1 = msg1.substring(out==1?2:3);
            if(msg1.equals(short2string(tpm.getSecretKey())))
                break;
            synchronize(out1, (short) out,input);
            input = logistical.GetSqueue(n*k,-1,1);
        }
        key = KeyGeneration.generateKey(tpm.getSecretKey(),bound);
        System.out.println("最终协商的秘钥："+key);
        socketConnection.clientClose();
        return key;
    }
    //根据交换得到的密钥进行aes加密信息
    public String encrpytMessage(String data) throws Exception {
        if(key.length()>=16)
            return AESUtils.encryptData(key.substring(0,16),data);
        return AESUtils.encryptData(key,data);
    }
    //根据交换得到的密钥解密信息
    public String decryptMessage(String cipher) throws Exception {
        if(key.length()>=16)
            return AESUtils.decryptData(key.substring(0,16),cipher);
        return AESUtils.decryptData(key,cipher);
    }
    //密钥更新
    //newkey为加密后的新密钥
    public boolean UpdateKey(String newkey) throws Exception {
        this.key = decryptMessage(newkey);
        return true;
    }
    //short[]转string
    public static String short2string(short[] nums){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<nums.length;i++)
            stringBuilder.append(nums[i]+",");
        String result = stringBuilder.toString();
        return result.substring(0,result.length()-1);
    }

    private int epochs = 1000;
    private ObtainParameter obtainParameter = new ObtainParameter();
    private double rate = 0.0;
    private int true_step = 0;
    private int false_step = 0;
    private Queue<Boolean> queue = new LinkedList<>();
    private int window_size = 50;
    private double window_rate = 0.8;


    //滑动窗口学习法
    private void synchronize(short out1 , short out2 , short[] input){

        if (out1 == out2) {
            if(queue.size()==window_size){
                if(queue.peek()) true_step--;
                else false_step--;
                true_step++;
                queue.add(true);
                queue.remove();
                obtainParameter.setList_window((double) true_step/window_size);
                double rate = true_step/window_size;
                int step = (int) ((window_rate-rate)/window_rate*(tpm.getRightBound()/2+1));
                if(rate<window_rate){
                    for(int i=0;i<tpm.getK();i++) {
                        tpm.hiddenLayer.getNeurons()[i].setStep(step);
                    }
                }else {
                    for(int i=0;i<tpm.getK();i++) {
                        tpm.hiddenLayer.getNeurons()[i].setStep(0);
                    }
                }

            }else {
                queue.add(true);
                true_step++;
            }
            tpm.train(input, out2);
        }else{
            if(queue.size()==window_size){
                if(queue.peek()) true_step--;
                else false_step--;
                false_step++;
                queue.add(false);
                queue.remove();
                obtainParameter.setList_window((double) true_step/window_size);
                double rate = true_step/window_size;
                int step = (int) ((window_rate-rate)/window_rate*(tpm.getRightBound()/2+1));
                if(rate<window_rate){
                    for(int i=0;i<tpm.getK();i++) {
                        tpm.hiddenLayer.getNeurons()[i].setStep(step);
                    }
                }else {
                    for(int i=0;i<tpm.getK();i++) {
                        tpm.hiddenLayer.getNeurons()[i].setStep(0);
                    }
                }
            }else {
                queue.add(false);
                false_step++;
            }
        }
    }
    public static void main(String[] args) throws Exception {
        ExchangeKeyClient exchangeKey = new ExchangeKeyClient();
        exchangeKey.createSocket(8111,"127.0.0.1");
        String key_client = exchangeKey.generateKey_Client();
        exchangeKey.command.addkey(exchangeKey.host,key_client);

        exchangeKey.command.loop_client(exchangeKey.host,8111);

        System.out.println("退出");
    }
}

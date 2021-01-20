import learning_algorithm.LearningParadigm;
import learning_algorithm.TPMTrainer;
import tree_parity_machine.TreeParityMachine;
import utils.Chaos.Logistical;
import utils.KeyGeneration;
import utils.LFSR.GateType;
import utils.LFSR.LFSR;
import utils.SocketConnection;

import java.io.IOException;

import static learning_algorithm.LearningParadigm.HEBBIAN;

/**
 * @author : wangxuan
 * @date : 16:57 2020/12/10 0010
 */
public class ExchangeKeyClient {

    //树型奇偶机参数
    private static int n=300;
    private static int k=6;
    private static int k1=3;
    private static int bound=3;
    TreeParityMachine tpm;
    private static LearningParadigm method = HEBBIAN;
    private static TPMTrainer trainer = new TPMTrainer();

    //协商后的密钥
    private String key;

    //socket相关
    private int port;
    private String host;
    private SocketConnection socketConnection;

    public boolean createSocket(int port,String host){
        this.port = port;
        this.host = host;
        socketConnection = new SocketConnection(port,host);
        return true;
    }
    public String generateKey_Server() throws IOException {
        String msg1 = socketConnection.listen(n+","+k+","+k1+","+bound,msg -> {
            System.out.println("协商后的参数: "+msg);
        });
        socketConnection.serverClose();
        tpm = new TreeParityMachine(n, k, k1,-bound, bound, method);
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
        String msg1 = socketConnection.send(n+","+k+","+k1+","+bound+'\0',msg -> {
            System.out.println("协商后的参数: "+msg);
        });
        //socketConnection.clientClose();
        tpm = new TreeParityMachine(n, k, k1,-bound, bound, method);

        //生成随机序列
//        LFSR lfsr = new LFSR ();
//        lfsr.setNumberOfBits ( n*k );
//        lfsr.setGateType (GateType.XOR);
//
//        for(int i=0;i<500;i++)
//            lfsr.strobeClock (); // Move the LFSR forward one.
          short[] input = new short[n*k];
//        String bits = lfsr.getBitsForward();
//        for(int i=0;i<n*k;i++){
//            input[i] = (short) (bits.charAt(i)=='0'?-1:1);
//        }

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
            //socketConnection.clientClose();
            int out = msg1.charAt(0)=='-'?-1:1;
            msg1 = msg1.substring(out==1?2:3);
            if(msg1.equals(short2string(tpm.getSecretKey())))
                break;
            if(out == out1)
                tpm.train(input,out1);

            //生成随机序列
//            for(int i=0;i<500;i++)
//                lfsr.strobeClock ();
//            input = new short[n*k];
//            bits = lfsr.getBitsForward();
//            for(int i=0;i<n*k;i++){
//                input[i] = (short) (bits.charAt(i)=='0'?-1:1);
//            }

            input = logistical.GetSqueue(n*k,-1,1);
        }
        key = KeyGeneration.generateKey(tpm.getSecretKey(),bound);
        System.out.println("最终协商的秘钥："+key);
        socketConnection.clientClose();
        return key;
    }
    public String encrpytMessage(){
        return null;
    }
    public String decryptMessage(){
        return null;
    }
    //short[]转string
    public static String short2string(short[] nums){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<nums.length;i++)
            stringBuilder.append(nums[i]+",");
        String result = stringBuilder.toString();
        return result.substring(0,result.length()-1);
    }
    public static void main(String[] args) throws Exception {
        ExchangeKeyClient exchangeKey = new ExchangeKeyClient();
        exchangeKey.createSocket(8111,"127.0.0.1");
        //exchangeKey.generateKey_Server();
        exchangeKey.generateKey_Client();
    }
}

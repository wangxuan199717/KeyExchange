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
public class ExchangeKey {

    //树型奇偶机参数
    private static int n=100;
    private static int k=3;
    private static int bound=3;
    private TreeParityMachine tpm;
    private static LearningParadigm method = HEBBIAN;
    private static TPMTrainer trainer = new TPMTrainer();

    //协商后的密钥
    private String key;

    //socket相关
    private int port;
    private String host;
    private SocketConnection socketConnection;

    //command相关
    private Command command = new Command();

    public boolean createSocket(int port, String host){
        this.port = port;
        this.host = host;
        socketConnection = new SocketConnection(port,host);
        return true;
    }
    public String generateKey_Server() throws Exception {
        queue.clear();
        obtainParameter.getList_window().clear();
        false_step = 0;
        true_step = 0;

        String msg1 = socketConnection.listen(n+","+k+","+bound+'\0', msg -> {
            System.out.println("协商后的参数: "+msg);
        });
        String[] param = msg1.split(",");
        n = Integer.parseInt(param[0]);
        k = Integer.parseInt(param[1]);
        bound = Integer.parseInt(param[2]);
        tpm = new TreeParityMachine(n,k,-bound,bound, method);
        short[] input;
        //协商的初始值
        double u=3.98;
        double x=0.5;
        int N=100;

        //双方的输入序列产生器
        Logistical logistical = new Logistical(u,x);
        logistical.GetSqueue(N,-1,1);
        input = logistical.GetSqueue(n*k,-1,1);

        long alltime = System.nanoTime();
        long startTime=System.nanoTime(); //获取开始时间
        //循环训练，成功则调出循环
        while (true) {
            startTime=System.nanoTime();
            short out1 = tpm.getOutput(input);
            System.out.print("计算输出时间:");
            System.out.println(System.nanoTime()-startTime+"ns");

            msg1 = socketConnection.listen(out1+","+short2string(tpm.getSecretKey()).hashCode()+'\0',msg -> {
                //System.out.println("接收到的数据："+msg);
            });
            int out = msg1.charAt(0)=='-'?-1:1;
            msg1 = msg1.substring(out==1?2:3);
            if(msg1.equals(String.valueOf(short2string(tpm.getSecretKey()).hashCode())))
                break;
            //System.out.println(msg1);
            //System.out.println(short2string(tpm.getSecretKey()));

            startTime = System.nanoTime();
            synchronize((short) out,out1,input);
            System.out.print("同步时间:");
            System.out.println(System.nanoTime()-startTime+"ns");

            startTime = System.nanoTime();
            input = logistical.GetSqueue(n*k,-1,1);
            System.out.print("计算输入向量时间:");
            System.out.println(System.nanoTime()-startTime+"ns");

            System.out.println("************************");
            System.out.println();

            System.out.println();
        }

        System.out.println("互学习次数："+time);
        time=0;
        long endTime=System.nanoTime(); //获取结束时间
        System.out.println("本次同步时间为："+(System.nanoTime()-alltime)+"ns");

        key = KeyGeneration.generateKey(tpm.getSecretKey(),bound);
        System.out.println("最终协商的秘钥："+key);
        System.out.println("*******************************************\n");
        socketConnection.serverClose();
        return key;
    }
    public String generateKey_Client() throws IOException {
        String msg1 = socketConnection.send(n+","+k+","+bound, msg -> {
            System.out.println("协商后的参数: "+msg);
        });
        socketConnection.clientClose();
        tpm = new TreeParityMachine(n, k, -bound, bound, method);

        //生成随机序列
        LFSR lfsr = new LFSR ();
        lfsr.setNumberOfBits ( n*k );
        lfsr.setGateType (GateType.XOR);
        for(int i=0;i<500;i++)
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
            msg1 = socketConnection.send(out1+","+short2string(tpm.getSecretKey()),msg -> {
                System.out.println("接收到的数据："+msg);
            });
            socketConnection.clientClose();
            short out = (short) msg1.charAt(0);
            msg1 = msg1.substring(2,msg1.length());
            if(out == out1) {
                tpm.train(input, out1);
            }
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
    private int time = 0;


    //滑动窗口学习法
    private void synchronize(short out1 , short out2 , short[] input){

        if (out1 == out2) {
            time++;
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
        while(true){
            //交换秘钥
            ExchangeKey exchangeKey = new ExchangeKey();
            exchangeKey.createSocket(8089,"127.0.0.1");
            String key_server = exchangeKey.generateKey_Server();

            //交换完成，开始通信
            exchangeKey.command.addkey(exchangeKey.getSocketConnection().ip,key_server);
            exchangeKey.command.loop();
            System.out.println("结束");
            //break;
        }
        //exchangeKey.generateKey_Client();
    }
    public static int getN() {
        return n;
    }

    public static void setN(int n) {
        ExchangeKey.n = n;
    }

    public static int getK() {
        return k;
    }

    public static void setK(int k) {
        ExchangeKey.k = k;
    }

    public static int getBound() {
        return bound;
    }

    public static void setBound(int bound) {
        ExchangeKey.bound = bound;
    }

    public TreeParityMachine getTpm() {
        return tpm;
    }

    public void setTpm(TreeParityMachine tpm) {
        this.tpm = tpm;
    }

    public static LearningParadigm getMethod() {
        return method;
    }

    public static void setMethod(LearningParadigm method) {
        ExchangeKey.method = method;
    }

    public static TPMTrainer getTrainer() {
        return trainer;
    }

    public static void setTrainer(TPMTrainer trainer) {
        ExchangeKey.trainer = trainer;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public SocketConnection getSocketConnection() {
        return socketConnection;
    }

    public void setSocketConnection(SocketConnection socketConnection) {
        this.socketConnection = socketConnection;
    }

    public int getEpochs() {
        return epochs;
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }

    public ObtainParameter getObtainParameter() {
        return obtainParameter;
    }

    public void setObtainParameter(ObtainParameter obtainParameter) {
        this.obtainParameter = obtainParameter;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public int getTrue_step() {
        return true_step;
    }

    public void setTrue_step(int true_step) {
        this.true_step = true_step;
    }

    public int getFalse_step() {
        return false_step;
    }

    public void setFalse_step(int false_step) {
        this.false_step = false_step;
    }

    public Queue<Boolean> getQueue() {
        return queue;
    }

    public void setQueue(Queue<Boolean> queue) {
        this.queue = queue;
    }

    public int getWindow_size() {
        return window_size;
    }

    public void setWindow_size(int window_size) {
        this.window_size = window_size;
    }

    public double getWindow_rate() {
        return window_rate;
    }

    public void setWindow_rate(double window_rate) {
        this.window_rate = window_rate;
    }
}

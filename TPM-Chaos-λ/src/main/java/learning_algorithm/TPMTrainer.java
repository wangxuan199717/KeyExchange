package learning_algorithm;

import Chaos.Logistical;
import Chaos.QueueWithGeo;
import tree_parity_machine.InputUnmatchException;
import tree_parity_machine.TreeParityMachine;
import tree_parity_machine.TreeParityMachineEve;
import utils.ObtainParameter;
import utils.Random;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * @author : wangxuan
 * @date : 8:56 2020/6/8 0008
 */
public class TPMTrainer {

    private int epochs = 1000;
    private ObtainParameter obtainParameter;
    private QueueWithGeo queueWithGeo = null;

    //混沌参数
    private double u;
    private double x;
    private int N;

    public ObtainParameter getObtainParameter() {
        return obtainParameter;
    }

    public void setObtainParameter(ObtainParameter obtainParameter) {
        this.obtainParameter = obtainParameter;
    }

    public int synchronize(TreeParityMachine tpm1, TreeParityMachine tpm2, TreeParityMachine tpm3) throws Exception {
        if (tpm1.getLearningParadigm() != tpm2.getLearningParadigm())
            throw new InputUnmatchException();
        short k = 0;
        int[] params = tpm1.getTPMParams();

        //协商的初始值
        u=3.98;
        x=0.5;
        N=100;

        //双方的输入序列产生器
        //Logistical logistical = new Logistical(u,x);
        //Logistical logistical1 = new Logistical(u,x);
        if(queueWithGeo==null)
            queueWithGeo = new QueueWithGeo(tpm1.getRightBound(),tpm1.getK(),tpm1.getN());

        //消除暂态效应
        //logistical.GetSqueue(N,-1,1);
        //logistical1.GetSqueue(N,-1,1);

        short[] input = new short[0];
        short[] input1 = new short[0];
        short[] input2 = new short[0];
        //short[] input;

        obtainParameter = new ObtainParameter(tpm1.getN(),tpm1.getK(),tpm1.getLeftBound(),tpm1.getRightBound());

        obtainParameter.getDistribution(tpm1.getSecretKey());
        //System.out.println(obtainParameter.getDistributionString());
        //循环训练，成功则调出循环
        boolean f = true;
        while (true) {
            //input = logistical.GetSqueue(params[0] * params[1],-1,1);

            if(k<100){
                input1 = Random.getInts(params[0] * params[1], -1, 1);
                input2 = input1;
            }else {
                if(f){
                    input2 = queueWithGeo.getSqueue(tpm1.getSecretKey());
                    f=false;
                }else {
                    input2 = queueWithGeo.getSqueue(tpm2.getSecretKey());
                    f=true;
                }
            }

            short out1 = tpm1.getOutput(input2);
            short out2 = tpm2.getOutput(input2);
            if (Arrays.equals(tpm1.getSecretKey(), tpm2.getSecretKey()))
                break;

            if (out1 == out2) {
                tpm1.train(input2, out1);
                tpm2.train(input2, out1);
                tpm3.train(input1, out1);

                //System.out.println("tpm1:"+Arrays.toString(tpm1.getSecretKey()));
                //System.out.println("tpm2:"+Arrays.toString(tpm2.getSecretKey()));

                k++;
                if(k>20000)
                    break;
                obtainParameter.getQa(tpm1.getSecretKey());
                obtainParameter.getQb(tpm2.getSecretKey());
                obtainParameter.getR(tpm1.getSecretKey(),tpm2.getSecretKey());
                obtainParameter.getP();
                obtainParameter.getAve_p();
                //System.out.println(obtainParameter.getAve_p());
                //System.out.println("第"+k+"次更新:");
                /*System.out.println(Arrays.toString(tpm1.getSecretKey()) + "\n" +
                                   Arrays.toString(tpm2.getSecretKey())+"\n"+
                                   Arrays.toString(tpm3.getSecretKey()));*/
            }
            //obtainParameter.getDistribution(tpm1.getSecretKey());
            //input = Random.getInts(params[0] * params[1], -1, 1);
        }
        List<Double> detap = obtainParameter.getDetap();
        for(int i=0;i<detap.size();i++){
            BigDecimal b = new BigDecimal(detap.get(i));
            double f1 = b.setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue();
            System.out.println(f1);
        }
        //System.out.println("同步参数最终值"+obtainParameter.getAve_p());
        return k;
    }

    public int synchronize1(TreeParityMachine tpm1, TreeParityMachine tpm2, TreeParityMachineEve tpm3) throws Exception {

        if (tpm1.getLearningParadigm() != tpm2.getLearningParadigm())
            throw new InputUnmatchException();
        short k = 0;
        int[] params = tpm1.getTPMParams();

        //协商的初始值
        u=3.98;
        x=0.5;
        N=100;

        //双方的输入序列产生器
        Logistical logistical = new Logistical(u,x);
        Logistical logistical1 = new Logistical(u,x);

        //消除暂态效应
        logistical.GetSqueue(N,-1,1);
        logistical1.GetSqueue(N,-1,1);

        short[] input1;
        short[] input;

        obtainParameter = new ObtainParameter(tpm1.getN(),tpm1.getK(),tpm1.getLeftBound(),tpm1.getRightBound());

        obtainParameter.getDistribution(tpm1.getSecretKey());
        //System.out.println(obtainParameter.getDistributionString());
        //循环训练，成功则调出循环
        while (true) {
            input = logistical.GetSqueue(params[0] * params[1],-1,1);
            input1 = logistical1.GetSqueue(params[0] * params[1],-1,1);
            short out1 = tpm1.getOutput(input);
            short out2 = tpm2.getOutput(input1);
            if (Arrays.equals(tpm1.getSecretKey(), tpm2.getSecretKey()))
                break;

            if (out1 == out2) {
                tpm1.train(input, out2);
                tpm2.train(input1, out1);
                tpm3.train(input1,out1);
                k++;
                obtainParameter.getQa(tpm1.getSecretKey());
                obtainParameter.getQb(tpm2.getSecretKey());
                obtainParameter.getR(tpm1.getSecretKey(),tpm2.getSecretKey());
                obtainParameter.getP();
                obtainParameter.getAve_p();

                //System.out.println(obtainParameter.getAve_p());

            }
            obtainParameter.getDistribution(tpm1.getSecretKey());
        }
//        List<Double> detap = obtainParameter.getDetap();
//        for(int i=0;i<detap.size();i++){
//            BigDecimal b = new BigDecimal(detap.get(i));
//            double f1 = b.setScale(10, BigDecimal.ROUND_HALF_UP).doubleValue();
//            System.out.println(f1);
//        }
        return k;
    }
    public void synchronize(TreeParityMachine tpm1, short[] input, short out2) {
        short out1 = tpm1.getOutput(input);
        if (out1 == out2)
            tpm1.train(input, out2);
    }

    public int getEpochs() {
        return epochs;
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }
}

package learning_algorithm;

import Excel.ExcelUtils;
import jxl.write.WriteException;
import tree_parity_machine.InputUnmatchException;
import tree_parity_machine.NeuralNetException;
import tree_parity_machine.TreeParityMachine;
import utils.ObtainParameter;
import utils.Random;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @author : wangxuan
 * @date : 8:56 2020/6/8 0008
 */
public class TPMTrainer {

    private int epochs = 1000;
    private ObtainParameter obtainParameter = new ObtainParameter();
    private double rate = 0.0;
    private int true_step = 0;
    private int false_step = 0;
    private Queue<Boolean> queue = new LinkedList<>();
    private int window_size = 50;
    private double window_rate = 0.8;


    public ObtainParameter getObtainParameter() {
        return obtainParameter;
    }

    public void setObtainParameter(ObtainParameter obtainParameter) {
        this.obtainParameter = obtainParameter;
    }

    public int synchronize(TreeParityMachine tpm1, TreeParityMachine tpm2, TreeParityMachine tpm3) {
        if (tpm1.getLearningParadigm() != tpm2.getLearningParadigm())
            throw new InputUnmatchException();
        short k = 0;
        int[] params = tpm1.getTPMParams();
        short[] input = Random.getInts(params[0] * params[1], -1, 1);

        obtainParameter = new ObtainParameter(tpm1.getN(),tpm1.getK(),tpm1.getLeftBound(),tpm1.getRightBound());

        obtainParameter.getDistribution(tpm1.getSecretKey());
        //System.out.println(obtainParameter.getDistributionString());
        //循环训练，成功则调出循环
//        if(tpm1.getLamda()!=-1){
//            tpm2.SmallScope(tpm2.getLamda());
//            tpm1.SmallScope(tpm1.getLamda());
//            tpm3.SmallScope(tpm3.getLamda());
//        }
        //初始化交换数据量
        obtainParameter.setNormalbit(0);
        obtainParameter.setNewrulebit(0);
        //
        obtainParameter.addNewrulebit(4+4+4);
        while (true) {
            obtainParameter.addNormalbit(input.length/8);

            short out1 = tpm1.getOutput(input);
            short out2 = tpm2.getOutput(input);
            if (Arrays.equals(tpm1.getSecretKey(), tpm2.getSecretKey()))
                break;

            if (out1 == out2) {
                true_step++;

                //假设使用MD5进行结果传输
                obtainParameter.addNewrulebit(16);
                obtainParameter.addNormalbit(16);

                tpm1.train(input, out2);
                tpm2.train(input, out1);
                tpm3.train(input,out1);
                k++;
                obtainParameter.getQa(tpm1.getSecretKey());
                obtainParameter.getQb(tpm2.getSecretKey());
                obtainParameter.getR(tpm1.getSecretKey(),tpm2.getSecretKey());
                obtainParameter.getP();
                //System.out.println("第"+k+"次更新:");
                /*System.out.println(Arrays.toString(tpm1.getSecretKey()) + "\n" +
                                   Arrays.toString(tpm2.getSecretKey())+"\n"+
                                   Arrays.toString(tpm3.getSecretKey()));*/
            }else
                false_step++;

            obtainParameter.getDistribution(tpm1.getSecretKey());
            input = Random.getInts(params[0] * params[1], -1, 1);
        }

        //加密生成随机的λ并保存,敌手未知
        //java.util.Random r = new java.util.Random();
        //double lamuda = r.nextDouble()/2+0.5;
        //tpm1.setLamda(lamuda);
        //tpm2.setLamda(lamuda);

        obtainParameter.addNewrulebit(4);

        //System.out.println("新规则："+obtainParameter.getNewrulebit());
        //System.out.println("旧规则："+obtainParameter.getNormalbit());

        //敌手随机生成λ
        //tpm3.setLamda(r.nextDouble());
        //System.out.println("同步参数最终值"+obtainParameter.getAve_p());
        return k;
    }

    public int synchronize1(TreeParityMachine tpm1, TreeParityMachine tpm2, TreeParityMachine tpm3) throws IOException, WriteException {
        if (tpm1.getLearningParadigm() != tpm2.getLearningParadigm())
            throw new InputUnmatchException();
        short k = 0;
        int[] params = tpm1.getTPMParams();
        short[] input = Random.getInts(params[0] * params[1], -1, 1);
        queue.clear();
        obtainParameter.getList_window().clear();
        false_step = 0;
        true_step = 0;
        ExcelUtils excelUtils = new ExcelUtils();

        while (true) {
            short out1 = tpm1.getOutput(input);
            short out2 = tpm2.getOutput(input);
            if (Arrays.equals(tpm1.getSecretKey(), tpm2.getSecretKey()))
                break;

            if (out1 == out2) {
                if(queue.size()==window_size){
                    if(queue.peek()) true_step--;
                    else false_step--;
                    true_step++;
                    queue.add(true);
                    queue.remove();
                    obtainParameter.setList_window((double) true_step/window_size);
                    double rate = true_step/window_size;
                    int step = (int) ((window_rate-rate)/window_rate*(tpm1.getRightBound()/2+1));
                    if(rate<window_rate){
                        for(int i=0;i<tpm1.getK();i++) {
                            tpm1.hiddenLayer.getNeurons()[i].setStep(step);
                            tpm2.hiddenLayer.getNeurons()[i].setStep(step);
                        }
                    }else {
                        for(int i=0;i<tpm1.getK();i++) {
                            tpm1.hiddenLayer.getNeurons()[i].setStep(0);
                            tpm2.hiddenLayer.getNeurons()[i].setStep(0);
                        }
                    }

                }else {
                    queue.add(true);
                    true_step++;
                }
                tpm1.train(input, out2);
                tpm2.train(input, out1);
                tpm3.train(input,out1);
                k++;
            }else{
                if(queue.size()==window_size){
                    if(queue.peek()) true_step--;
                    else false_step--;
                    false_step++;
                    queue.add(false);
                    queue.remove();
                    obtainParameter.setList_window((double) true_step/window_size);
                    double rate = true_step/window_size;
                    int step = (int) ((window_rate-rate)/window_rate*(tpm1.getRightBound()/2+1));
                    if(rate<window_rate){
                        for(int i=0;i<tpm1.getK();i++) {
                            tpm1.hiddenLayer.getNeurons()[i].setStep(step);
                            tpm2.hiddenLayer.getNeurons()[i].setStep(step);
                        }
                    }else {
                        for(int i=0;i<tpm1.getK();i++) {
                            tpm1.hiddenLayer.getNeurons()[i].setStep(0);
                            tpm2.hiddenLayer.getNeurons()[i].setStep(0);
                        }
                    }
                }else {
                    queue.add(false);
                    false_step++;
                }
            }
            input = Random.getInts(params[0] * params[1], -1, 1);
        }
        //excelUtils.Write(obtainParameter.getList_window().toArray(),1);
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

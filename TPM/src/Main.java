import jxl.write.WriteException;
import learning_algorithm.LearningParadigm;
import learning_algorithm.TPMTrainer;
import tree_parity_machine.TreeParityMachine;
import utils.KeyGeneration;
import utils.ObtainParameter;

import java.io.IOException;
import java.util.Arrays;

import static learning_algorithm.LearningParadigm.HEBBIAN;

public class Main {
    public static final int n=100;
    public static final int k=3;
    public static final int bound=3;
    public static final LearningParadigm method = HEBBIAN;
    public static ObtainParameter obtainer = new ObtainParameter(n,k,-bound,bound);
    public static boolean printw = false;
    //public static ExcelUtils excelUtils = new ExcelUtils();

    public static void main(String[] args) throws IOException, WriteException {
        //Common();
        Common_Window();
//        NewRule();
//        NewRule();
//        for(int i=1;i<9;i++){
//            CommonWithN(5,i*50,4);
//        }
    }

    //普通的训练
    public static void NewRule() throws IOException, WriteException {
        TPMTrainer trainer = new TPMTrainer();
        int total=500;
        int rate=0;
        int av_step=0;

        double lamuda=-1;
        double lamuda1=-1;

        for(int i=0;i<total;i++){
            //秘钥协商双方
            TreeParityMachine tpm1 = new TreeParityMachine(n, k, -bound, bound, method);
            TreeParityMachine tpm2 = new TreeParityMachine(n, k, -bound, bound, method);
            //敌手
            TreeParityMachine tpm3 = new TreeParityMachine(n, k, -bound, bound, method);

            if(lamuda!=-1){
                tpm1.SmallScope(lamuda);
                tpm2.SmallScope(lamuda);
                //tpm3.SmallScope(lamuda1);
            }
            int step = trainer.synchronize(tpm1, tpm2, tpm3);
            //ObtainParameter obtainParameter = trainer.getObtainParameter();
            //excelUtils.Write(obtainParameter.list.toArray(),2);
            //obtainParameter.getDistribution(tpm1.getSecretKey());
            //System.out.println(obtainParameter.getDistributionString());
            av_step+=step;
            boolean flag = Arrays.toString(tpm1.getSecretKey()).equals(Arrays.toString(tpm3.getSecretKey()));
            System.out.println("本次攻击是否成功："+flag+"训练次数："+step+"  lamuda:"+tpm1.getLamda());
            if(flag) rate++;

            //输出敌手和双方的权值
            if(printw){
                System.out.println("tpm1:"+Arrays.toString(tpm1.getSecretKey()));
                System.out.println("tpm2:"+Arrays.toString(tpm2.getSecretKey()));
                System.out.println("tpm3:"+Arrays.toString(tpm3.getSecretKey()));
            }
            lamuda=tpm1.getLamda();
            lamuda1=tpm3.getLamda();

            //obtainer.getDistribution(tpm1.getSecretKey());
            //System.out.println(Arrays.toString(tpm1.getSecretKey()) + "\n" + Arrays.toString(tpm2.getSecretKey())+"\n"+ Arrays.toString(tpm3.getSecretKey()));
        }
        System.out.println("攻击成功率："+(double) rate/total);
        System.out.println("平均训练成功次数："+av_step/total);
    }
    public static void Common() throws IOException, WriteException {
        TPMTrainer trainer = new TPMTrainer();
        int total=20;
        int rate=0;
        int av_step=0;

        for(int i=0;i<total;i++){
            //秘钥协商双方
            TreeParityMachine tpm1 = new TreeParityMachine(n, k, -bound, bound, method);
            TreeParityMachine tpm2 = new TreeParityMachine(n, k, -bound, bound, method);
            //敌手
            TreeParityMachine tpm3 = new TreeParityMachine(n, k, -bound, bound, method);
            int step = trainer.synchronize(tpm1, tpm2, tpm3);
            av_step+=step;
            boolean flag = Arrays.toString(tpm1.getSecretKey()).equals(Arrays.toString(tpm3.getSecretKey()));
            System.out.println("本次攻击是否成功："+flag+"训练次数："+step+"  lamuda:"+tpm1.getLamda());
            if(flag) rate++;

            System.out.println(KeyGeneration.generateKey(tpm1.getSecretKey(),bound));
            System.out.println(KeyGeneration.generateKey(tpm2.getSecretKey(),bound));
            System.out.println(KeyGeneration.generateKey(tpm3.getSecretKey(),bound));
            //输出敌手和双方的权值
            if(printw){
                System.out.println("tpm1:"+Arrays.toString(tpm1.getSecretKey()));
                System.out.println("tpm2:"+Arrays.toString(tpm2.getSecretKey()));
                System.out.println("tpm3:"+Arrays.toString(tpm3.getSecretKey()));
            }
        }
        System.out.println("攻击成功率："+(double) rate/total);
        System.out.println("平均训练成功次数："+av_step/total);
    }

    //使用动态学习率方式
    public static void Common_Window() throws IOException, WriteException {
        TPMTrainer trainer = new TPMTrainer();
        int total=100;
        int rate=0;
        int av_step=0;

        for(int i=0;i<total;i++){
            //秘钥协商双方
            TreeParityMachine tpm1 = new TreeParityMachine(n, k, -bound, bound, method);
            TreeParityMachine tpm2 = new TreeParityMachine(n, k, -bound, bound, method);
            //敌手
            TreeParityMachine tpm3 = new TreeParityMachine(n, k, -bound, bound, method);
            int step = trainer.synchronize1(tpm1, tpm2, tpm3);
            av_step+=step;
            boolean flag = Arrays.toString(tpm1.getSecretKey()).equals(Arrays.toString(tpm3.getSecretKey()));
            System.out.println("本次攻击是否成功："+flag+"训练次数："+step+"  lamuda:"+tpm1.getLamda());
            if(flag) rate++;

            System.out.println(KeyGeneration.generateKey(tpm1.getSecretKey(),bound));

            //输出敌手和双方的权值
            if(printw){
                System.out.println("tpm1:"+Arrays.toString(tpm1.getSecretKey()));
                System.out.println("tpm2:"+Arrays.toString(tpm2.getSecretKey()));
                System.out.println("tpm3:"+Arrays.toString(tpm3.getSecretKey()));
            }
        }
        System.out.println("攻击成功率："+(double) rate/total);
        System.out.println("平均训练成功次数："+av_step/total);
    }

    public static void CommonWithN(int K,int N,int Bound) throws IOException, WriteException {
        TPMTrainer trainer = new TPMTrainer();
        int total=50;
        int rate=0;
        int av_step=0;

        for(int i=0;i<total;i++){
            //秘钥协商双方
            TreeParityMachine tpm1 = new TreeParityMachine(N, K, -Bound, Bound, method);
            TreeParityMachine tpm2 = new TreeParityMachine(N, K, -Bound, Bound, method);
            //敌手
            TreeParityMachine tpm3 = new TreeParityMachine(N, K, -Bound, Bound, method);

            int step = trainer.synchronize(tpm1, tpm2, tpm3);
            //ObtainParameter obtainParameter = trainer.getObtainParameter();
            //excelUtils.Write(obtainParameter.list.toArray(),2);
            //obtainParameter.getDistribution(tpm1.getSecretKey());
            //System.out.println(obtainParameter.getDistributionString());
            av_step+=step;
            boolean flag = Arrays.toString(tpm1.getSecretKey()).equals(Arrays.toString(tpm3.getSecretKey()));
            //System.out.println("本次攻击是否成功："+flag+"训练次数："+step);
            if(flag) rate++;

            //obtainer.getDistribution(tpm1.getSecretKey());
            //System.out.println(Arrays.toString(tpm1.getSecretKey()) + "\n" + Arrays.toString(tpm2.getSecretKey())+"\n"+ Arrays.toString(tpm3.getSecretKey()));
        }
        //System.out.println("攻击成功率："+(double) rate/total);
        System.out.println("平均训练成功次数："+av_step/total);
    }

    //限制初始值的训练：敌手知道我们限制的范围
    public static void SmallW(double lamuda){
        TPMTrainer trainer = new TPMTrainer();
        int total=500;
        int rate=0;
        int av_step=0;

        for(int i=0;i<total;i++){
            //秘钥协商双方
            TreeParityMachine tpm1 = new TreeParityMachine(n, k, -bound, bound, method);
            TreeParityMachine tpm2 = new TreeParityMachine(n, k, -bound, bound, method);

            tpm1.SmallScope(lamuda);
            tpm2.SmallScope(lamuda);
            //敌手
            TreeParityMachine tpm3 = new TreeParityMachine(n, k, -bound, bound, method);
            //将λ告诉敌手
            tpm3.SmallScope(lamuda);

            int step = trainer.synchronize(tpm1, tpm2, tpm3);
            av_step+=step;
            boolean flag = Arrays.toString(tpm1.getSecretKey()).equals(Arrays.toString(tpm3.getSecretKey()));
            //System.out.println("本次攻击是否成功："+flag+"训练次数："+step);
            if(flag) rate++;
            if(printw){
                System.out.println("tpm1:"+Arrays.toString(tpm1.getSecretKey()));
                System.out.println("tpm2:"+Arrays.toString(tpm2.getSecretKey()));
                System.out.println("tpm3:"+Arrays.toString(tpm3.getSecretKey()));
            }
            //obtainer.getDistribution(tpm1.getSecretKey());
            //System.out.println(obtainer.getDistributionString());
            //System.out.println(Arrays.toString(tpm1.getSecretKey()) + "\n" + Arrays.toString(tpm2.getSecretKey())+"\n"+ Arrays.toString(tpm3.getSecretKey()));
        }
        System.out.println("攻击成功率："+(double) rate/total);
        System.out.println("平均训练成功次数："+av_step/total);
    }

    //限制初始值的训练:敌手不知道我们的限制范围
    public static void SmallW1(double lamuda){
        TPMTrainer trainer = new TPMTrainer();
        int total=500;
        int rate=0;
        int av_step=0;

        for(int i=0;i<total;i++){
            //秘钥协商双方
            TreeParityMachine tpm1 = new TreeParityMachine(n, k, -bound, bound, method);
            TreeParityMachine tpm2 = new TreeParityMachine(n, k, -bound, bound, method);

            tpm1.SmallScope(lamuda);
            tpm2.SmallScope(lamuda);
            //敌手
            TreeParityMachine tpm3 = new TreeParityMachine(n, k, -bound, bound, method);
            //敌手未知λ的值，λ的值假设通过秘密信道传输
            //tpm3.SmallScope(lamuda);

            int step = trainer.synchronize(tpm1, tpm2, tpm3);
            av_step+=step;
            boolean flag = Arrays.toString(tpm1.getSecretKey()).equals(Arrays.toString(tpm3.getSecretKey()));
            System.out.println("本次攻击是否成功："+flag+"训练次数："+step);
            if(flag) rate++;

            if(printw){
                System.out.println("tpm1:"+Arrays.toString(tpm1.getSecretKey()));
                System.out.println("tpm2:"+Arrays.toString(tpm2.getSecretKey()));
                System.out.println("tpm3:"+Arrays.toString(tpm3.getSecretKey()));
            }
            //obtainer.getDistribution(tpm1.getSecretKey());
            //System.out.println(obtainer.getDistributionString());
            //System.out.println(Arrays.toString(tpm1.getSecretKey()) + "\n" + Arrays.toString(tpm2.getSecretKey())+"\n"+ Arrays.toString(tpm3.getSecretKey()));
        }
        System.out.println("攻击成功率："+(double) rate/total);
        System.out.println("平均训练成功次数："+av_step/total);
    }

}

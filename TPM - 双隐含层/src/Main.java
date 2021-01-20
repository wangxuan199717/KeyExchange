import learning_algorithm.LearningParadigm;
import learning_algorithm.TPMTrainer;
import tree_parity_machine.TreeParityMachine;
import java.util.Arrays;

import static learning_algorithm.LearningParadigm.HEBBIAN;
import static learning_algorithm.LearningParadigm.RANDOM_WALK;

public class Main {
    public static final int n=10;
    //各个隐含层的数量
    public static final int k=6;
    public static final int k1=3;
    public static final int bound=3;
    public static final LearningParadigm method = HEBBIAN;

    public static void main(String[] args) {
        TPMTrainer trainer = new TPMTrainer();
        int total=500;
        int rate=0;
        int av_step=0;

        for(int i=0;i<total;i++){
            //秘钥协商双方
            TreeParityMachine tpm1 = new TreeParityMachine(n, k, k1,-bound, bound, method);
            TreeParityMachine tpm2 = new TreeParityMachine(n, k, k1, -bound, bound, method);
            //敌手
            TreeParityMachine tpm3 = new TreeParityMachine(n, k, k1, -bound, bound, method);

            int step = trainer.synchronize(tpm1, tpm2, tpm3);
            av_step+=step;
            boolean flag = Arrays.toString(tpm1.getSecretKey()).equals(Arrays.toString(tpm3.getSecretKey()));
            System.out.println("本次攻击是否成功："+flag+"训练次数："+step);

            int[] times = new int[bound*2+1];
            for(int j=0;j<tpm1.getSecretKey().length;j++){
                times[tpm1.getSecretKey()[j]+bound]++;
            }
            //System.out.println(Arrays.toString(tpm1.getSecretKey()));
            //System.out.println(Arrays.toString(tpm2.getSecretKey()));
            //System.out.println(Arrays.toString(tpm3.getSecretKey()));
            if(flag) rate++;
            //System.out.println(Arrays.toString(tpm1.getSecretKey()) + "\n" + Arrays.toString(tpm2.getSecretKey())+"\n"+ Arrays.toString(tpm3.getSecretKey()));
        }
        System.out.println("成功率："+(double) rate/total);
        System.out.println("平均训练成功次数："+av_step/total);

    }

}

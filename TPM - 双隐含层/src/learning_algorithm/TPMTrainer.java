package learning_algorithm;

import tree_parity_machine.InputUnmatchException;
import tree_parity_machine.TreeParityMachine;
import utils.Random;

import java.io.IOException;
import java.util.Arrays;

public class TPMTrainer {

    private int epochs = 1000;

    public int synchronize(TreeParityMachine tpm1, TreeParityMachine tpm2, TreeParityMachine tpm3) {
        if (tpm1.getLearningParadigm() != tpm2.getLearningParadigm())
            throw new InputUnmatchException();
        short k = 0;
        int[] params = tpm1.getTPMParams();
        short[] input = Random.getInts(params[0] * params[1], -1, 1);

        //循环训练，成功则调出循环
        while (true) {
            short out1 = tpm1.getOutput(input);
            short out2 = tpm2.getOutput(input);
            if (Arrays.equals(tpm1.getSecretKey(), tpm2.getSecretKey()))
                break;
            if (out1 == out2) {
                tpm1.train(input, out2);
                tpm2.train(input, out1);
                tpm3.train(input,out1);
                k++;
                //System.out.println("第"+k+"次更新:");
                /*System.out.println(Arrays.toString(tpm1.getSecretKey()) + "\n" +
                                   Arrays.toString(tpm2.getSecretKey())+"\n"+
                                   Arrays.toString(tpm3.getSecretKey()));*/
            }
            input = Random.getInts(params[0] * params[1], -1, 1);
        }
        return k;
    }

    public int synchronize_socket(TreeParityMachine tpm1,int out,String hash) throws IOException {
        int[] params = tpm1.getTPMParams();
        short[] input = Random.getInts(params[0] * params[1], -1, 1);

        //循环训练，成功则调出循环
        while (true) {
            short out1 = tpm1.getOutput(input);
            System.out.println(out+" "+out1);
            if (out1 == out) {
                tpm1.train(input, out1);
            }
            if(hash == tpm1.getSecretKey().toString()){
                break;
            }
            input = Random.getInts(params[0] * params[1], -1, 1);
        }
        return 0;
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

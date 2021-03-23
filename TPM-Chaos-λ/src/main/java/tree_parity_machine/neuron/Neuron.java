package tree_parity_machine.neuron;

import learning_algorithm.LearningParadigm;
import tree_parity_machine.NeuralNetException;
import tree_parity_machine.TreeParityMachine;

import java.util.Arrays;

/**
 * @author : wangxuan
 * @date : 8:56 2020/6/8 0008
 */
public abstract class Neuron {

    protected short output;
    protected int inputs;
    protected short[] weights;
    protected int leftBound;
    protected int rightBound;
    protected LearningParadigm paradigm;
    protected int step = 0;

    public void setStep(int s){
        step = s;
    }


    public abstract void init();

    public abstract void changeWeights(short[] input, short outputTPM);

    public abstract short getOutput(short[] input);

    public short[] getWeights() {
        return weights;
    }

    public LearningParadigm getParadigm() {
        return paradigm;
    }

    public void setParadigm(LearningParadigm paradigm) {
        this.paradigm = paradigm;
    }

    @Override
    public String toString() {
        return "Neuron{" +
                "output=" + output +
                ", inputs=" + inputs +
                ", weights=" + Arrays.toString(weights) +
                ", leftBound=" + leftBound +
                ", rightBound=" + rightBound +
                ", paradigm=" + paradigm +
                '}';
    }
    public short getOutputwithoutSgn(short[] input) {
        return 0;
    }
}

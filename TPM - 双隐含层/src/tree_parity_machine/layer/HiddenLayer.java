package tree_parity_machine.layer;

import learning_algorithm.LearningParadigm;
import tree_parity_machine.InputUnmatchException;
import tree_parity_machine.NeuralNetException;
import tree_parity_machine.neuron.HiddenNeuron;
import tree_parity_machine.neuron.Neuron;

import java.util.Arrays;

public class HiddenLayer extends NetLayer {

    private short[] res;
    private double[] p;

    public short[] getRes() {
        return res;
    }

    public void setRes(short[] res) {
        this.res = res;
    }

    public double[] getP() {
        return p;
    }

    public void setP(double[] p) {
        this.p = p;
    }

    public double getAve_p() {
        return ave_p;
    }

    public void setAve_p(double ave_p) {
        this.ave_p = ave_p;
    }

    private double ave_p;

    public HiddenLayer(int n, int k, int leftBound, int rightBound, LearningParadigm paradigm) {
        inputs = n;
        outputs = k;
        this.paradigm = paradigm;
        neurons = new Neuron[k];
        for (int i = 0; i < k; i++) {
            neurons[i] = new HiddenNeuron(n, leftBound, rightBound, paradigm);
            neurons[i].init();
        }
    }

    public short[] getOutput(short[] input) {
        if (input.length != inputs * outputs)
            throw new InputUnmatchException();
        short[] res = new short[outputs];
        for (int i = 0; i < outputs; i++)
            res[i] = neurons[i].getOutput(Arrays.copyOfRange(input, inputs * i, inputs * (i + 1)));
        this.res = res;
        return res;
    }

    @Override
    public String toString() {
        return "HiddenLayer{" +
                "res=" + Arrays.toString(res) +
                ", outputs=" + outputs +
                ", inputs=" + inputs +
                ", neurons=" + Arrays.toString(neurons) +
                ", paradigm=" + paradigm +
                '}';
    }
}

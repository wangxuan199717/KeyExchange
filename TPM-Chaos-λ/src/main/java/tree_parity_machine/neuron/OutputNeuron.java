package tree_parity_machine.neuron;

import tree_parity_machine.NeuralNetException;

/**
 * @author : wangxuan
 * @date : 8:56 2020/6/8 0008
 */
public class OutputNeuron extends Neuron {

    public OutputNeuron(int inputs) {
        this.inputs = inputs;
        weights = new short[inputs];
    }

    public void init() {
        for (int i = 0; i < inputs; i++)
            weights[i] = 1;
    }

    @Override
    public void changeWeights(short[] input, short outputTPM) {

    }

    public short getOutput(short[] input) {
        if (input == null || input.length != inputs)
            throw new NeuralNetException();
        short res = 1;
        for (int i = 0; i < inputs; i++)
            res *= input[i];
        output = res;
        return res;
    }

    @Override
    public short getOutputwithoutSgn(short[] input) {
        return 0;
    }

}

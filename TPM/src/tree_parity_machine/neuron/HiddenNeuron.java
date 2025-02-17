package tree_parity_machine.neuron;

import learning_algorithm.LearningParadigm;
import tree_parity_machine.InputUnmatchException;
import tree_parity_machine.NeuralNetException;
import utils.Random;

/**
 * @author : wangxuan
 * @date : 8:56 2020/6/8 0008
 */
public class HiddenNeuron extends Neuron {

    public HiddenNeuron(int inputs, int leftBound, int rightBound, LearningParadigm paradigm) {
        this.inputs = inputs;
        this.paradigm = paradigm;
        weights = new short[inputs];
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }

    public void init() {
        for (int i = 0; i < inputs; i++)
            weights[i] = Random.getInt(leftBound, rightBound);
    }

    @Override
    public void changeWeights(short[] input, short outputTPM) {
        for (int i = 0; i < input.length; i++) {
            int dW = input[i] * outputTPM;
            dW = dW>0?(dW+step):(dW-step);
            switch (paradigm) {
                case HEBBIAN:
                    weights[i] += dW;
                    break;
                case ANTI_HEBBIAN:
                    weights[i] -= dW;
                    break;
                case RANDOM_WALK:
                    weights[i] += input[i];
                    break;
            }
            if (weights[i] > rightBound)
                weights[i] = (short) rightBound;
            else if (weights[i] < leftBound)
                weights[i] = (short) leftBound;
        }
    }

    public short getOutput(short[] input) {
        if (input == null || input.length != inputs)
            throw new InputUnmatchException();
        short sum = 0;
        for (int i = 0; i < inputs; i++)
            sum += weights[i] * input[i];
        output = (short) (sum > 0 ? 1 : -1);
        return output;
    }

}

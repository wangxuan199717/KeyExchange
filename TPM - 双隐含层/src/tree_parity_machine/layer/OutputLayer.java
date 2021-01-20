package tree_parity_machine.layer;

import tree_parity_machine.InputUnmatchException;
import tree_parity_machine.NeuralNetException;
import tree_parity_machine.neuron.Neuron;
import tree_parity_machine.neuron.OutputNeuron;

public class OutputLayer extends NetLayer {

    public OutputLayer(int k) {
        inputs = k;
        outputs = 1;
        neurons = new Neuron[1];
        neurons[0] = new OutputNeuron(k);
        neurons[0].init();
    }

    public short getOutput(short[] input) {
        if (input.length != inputs)
            throw new InputUnmatchException();
        return neurons[0].getOutput(input);
    }
}

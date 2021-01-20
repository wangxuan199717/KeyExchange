package tree_parity_machine.layer;

import learning_algorithm.LearningParadigm;
import tree_parity_machine.neuron.Neuron;

import java.util.Arrays;

/**
 * @author : wangxuan
 * @date : 8:56 2020/6/8 0008
 */
public abstract class NetLayer {

    protected int outputs;
    protected int inputs;
    protected Neuron[] neurons;
    protected LearningParadigm paradigm;

    public Neuron[] getNeurons() {
        return neurons;
    }

    public LearningParadigm getParadigm() {
        return paradigm;
    }

    public void setParadigm(LearningParadigm paradigm) {
        this.paradigm = paradigm;
    }

    @Override
    public String toString() {
        return "NetLayer{" +
                "neurons=" + Arrays.toString(neurons) +
                ", outputs=" + outputs +
                ", inputs=" + inputs +
                '}';
    }
}

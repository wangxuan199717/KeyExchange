package tree_parity_machine;

import learning_algorithm.LearningParadigm;
import learning_algorithm.TPMTrainer;
import learning_algorithm.Training;
import tree_parity_machine.layer.HiddenLayer;
import tree_parity_machine.layer.OutputLayer;
import tree_parity_machine.neuron.Neuron;

import java.util.Arrays;


/**
 * @author : wangxuan
 * @date : 8:56 2020/6/8 0008
 */
public class TreeParityMachine extends TPMTrainer implements Training {

    private int n;
    private int k;
    private int leftBound;
    private int rightBound;
    public HiddenLayer hiddenLayer;
    public OutputLayer outputLayer;
    private LearningParadigm paradigm;
    private double lamda=-1;

    public double getLamda() {
        return lamda;
    }

    public void setLamda(double lamda) {
        this.lamda = lamda;
    }

    public int getLeftBound() {
        return leftBound;
    }

    public void setLeftBound(int leftBound) {
        this.leftBound = leftBound;
    }

    public int getRightBound() {
        return rightBound;
    }

    public void setRightBound(int rightBound) {
        this.rightBound = rightBound;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public TreeParityMachine(int n, int k, int leftBound, int rightBound, LearningParadigm learningParadigm) {
        this.n = n;
        this.k = k;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.paradigm = learningParadigm;
        hiddenLayer = new HiddenLayer(n, k, leftBound, rightBound, paradigm);
        outputLayer = new OutputLayer(k);
    }
    //添加限制初值的优化
    public void SmallScope(double lamda){
        this.lamda = lamda;
        hiddenLayer = new HiddenLayer(n, k, (int) (lamda*leftBound), (int) (lamda*rightBound), paradigm);
    }

    public short getOutput(short[] input) {
        return outputLayer.getOutput(hiddenLayer.getOutput(input));
    }

    @Override
    public void train(short[] input, short output) {
        Neuron[] hiddenNeurons = hiddenLayer.getNeurons();
        for (int i = 0; i < k; i++)
            //只有隐含层与输出层相等时才进行训练
            if(hiddenNeurons[i].getOutput(Arrays.copyOfRange(input, n * i, n * (i + 1)))==output)
                hiddenNeurons[i].changeWeights(Arrays.copyOfRange(input, n * i, n * (i + 1)), output);
    }

    public short[] getSecretKey() {
        short[] key = new short[n * k];
        Neuron[] neurons = hiddenLayer.getNeurons();
        for (int i = 0; i < k; i++) {
            short[] mas = neurons[i].getWeights();
            if (n >= 0) System.arraycopy(mas, 0, key, i * n, n);
        }
        return key;
    }

    public LearningParadigm getLearningParadigm() {
        return paradigm;
    }

    public void setLearningParadigm(LearningParadigm paradigm) {
        this.paradigm = paradigm;
    }

    public int[] getTPMParams() {
        return new int[]{n, k};
    }

    @Override
    public String toString() {
        return "TreeParityMachine{" +
                "n=" + n +
                ", k=" + k +
                ", leftBound=" + leftBound +
                ", rightBound=" + rightBound +
                ", hiddenLayer=" + hiddenLayer +
                ", outputLayer=" + outputLayer +
                ", paradigm=" + paradigm +
                '}';
    }
}

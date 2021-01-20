package tree_parity_machine;

import learning_algorithm.LearningParadigm;
import learning_algorithm.Training;
import tree_parity_machine.layer.HiddenLayer;
import tree_parity_machine.layer.OutputLayer;
import tree_parity_machine.neuron.Neuron;

import java.util.Arrays;

public class TreeParityMachine implements Training {

    //参数

    //输入向量和隐含神经元个数
    private int n;
    private int n1;
    private int k;
    private int k1;

    //边界
    private int leftBound;
    private int rightBound;

    //隐含层
    private HiddenLayer hiddenLayer;
    private HiddenLayer hiddenLayer1;

    //输出层
    private OutputLayer outputLayer;

    //训练算法
    private LearningParadigm paradigm;

    public TreeParityMachine(int n, int k,int k1, int leftBound, int rightBound, LearningParadigm learningParadigm) {
        this.n = n;
        this.k = k;
        this.k1 = k1;
        n1 = k/k1;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
        this.paradigm = learningParadigm;
        hiddenLayer = new HiddenLayer(n, k, leftBound, rightBound, paradigm);
        outputLayer = new OutputLayer(k1);

        //隐含层2初始化
        if(k%k1!=0) throw new InputUnmatchException();
        this.hiddenLayer1 = new HiddenLayer(n1,k1,1,1,paradigm);
    }

    public short getOutput(short[] input) {
        return outputLayer.getOutput(
                hiddenLayer1.getOutput(
                        hiddenLayer.getOutput(input)));
    }

    @Override
    public void train(short[] input, short output) {
        Neuron[] hiddenNeurons = hiddenLayer.getNeurons();
        short[] hidenoutput = hiddenLayer.getOutput(input);
        short[] hidenoutput1 = hiddenLayer1.getOutput(hidenoutput);
        for(int j=0;j<k1;j++){
            //只有隐含层与输出层相等时才进行训练
            if(hidenoutput1[j]==output){
                for (int i = 0; i < k/k1; i++){
                    int pos = i+j*k/k1;
                    //只有第二层隐含层和输出层相等菜进行
                    if(hidenoutput[pos]==output)
                        hiddenNeurons[pos].changeWeights(Arrays.copyOfRange(input, n * pos, n *( pos + 1)), output);
                }
            }
        }
//        hiddenNeurons = hiddenLayer1.getNeurons();
//        for(int i=0;i<k1;i++){
//            //只有隐含层与输出层相等时才进行训练
//            if(hidenoutput1[i]==output)
//                hiddenNeurons[i].changeWeights(Arrays.copyOfRange(hidenoutput, n1 * i, n1 * (i + 1)), output);
//         }
    }

    public short[] getSecretKey() {
        short[] key = new short[n * k + k];
        Neuron[] neurons = hiddenLayer.getNeurons();
        for (int i = 0; i < k; i++) {
            short[] mas = neurons[i].getWeights();
            if (n >= 0) System.arraycopy(mas, 0, key, i * n, n);
        }
        //隐含层2的权值拷贝
        neurons = hiddenLayer1.getNeurons();
        for(int i=0;i<k1;i++){
            short[] mas = neurons[i].getWeights();
            if (n >= 0) System.arraycopy(mas, 0, key, n*k+i * n1, n1);
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
        return new int[]{n, k, k1};
    }

    @Override
    public String toString() {
        return "TreeParityMachine{" +
                "n=" + n +
                ", k=" + k +
                ", leftBound=" + leftBound +
                ", rightBound=" + rightBound +
                ", hiddenLayer=" + hiddenLayer +
                ", hiddenLayer1=" + hiddenLayer1 +
                ", outputLayer=" + outputLayer +
                ", paradigm=" + paradigm +
                '}';
    }
}

package tree_parity_machine;


import utils.KeyGeneration;

/**
 * @author : wangxuan
 * @date : 8:56 2020/6/8 0008
 */
public class NeuralNetException extends RuntimeException{
    public NeuralNetException() {
        super("网络不匹配！");
    }
}

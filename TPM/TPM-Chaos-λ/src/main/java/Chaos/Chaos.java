package Chaos;

import learning_algorithm.Training;

import java.util.List;

/**
 * @author : wangxuan
 * @date : 10:10 2020/6/10 0010
 */
public interface Chaos<T> {
    public List<T> GetSqueue(int length);
    public short[] GetSqueue(int length,int left,int right);
}

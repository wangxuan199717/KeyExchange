package Chaos_System;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : wangxuan
 * @date : 10:14 2020/6/10 0010
 */

/*
* 一维的混沌序列
* Xn+1=Xn×μ×(1-Xn)  μ∈[0,4]  X∈[0,1]
* */
public class Logistical implements Chaos<Double> {
    private double u;
    private double x0;
    private int length=1;
    private double max=0.0;
    private double min=999.0;
    private List<Double> squeue = new ArrayList<>();

    public Logistical(double u,double x0) throws Exception {
        if(u>=4 || u<=0)
            throw new Exception("参数u错误");
        if(x0>=1 || x0<=0)
            throw new Exception("参数x0错误");
        this.u = u;
        this.x0 = x0;
        squeue.add(x0);
    }

    @Override
    public List<Double> GetSqueue(int length) {
        if(this.length>length){
            return squeue.subList(0,length);
        }
        Double xn=0.0;
        for(int i=this.length;i<length;i++){
            xn = squeue.get(i-1);
            squeue.add(u*xn*(1-xn));
            if(max<xn)
                max=xn;
            if(min>xn)
                min=xn;
        }
        this.length=length;
        return squeue;
    }
    public char[][] convey(List<Double> list,int width,int height){
        char[][] result = new char[height][width];
        double between = max-min;

        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                result[i][j] =(char) ((list.get(i*width+j)-min)/between*256);
            }
        }
        return result;
    }
}

package utils;

import tree_parity_machine.InputUnmatchException;
import tree_parity_machine.TreeParityMachine;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : wangxuan
 * @date : 8:56 2020/6/8 0008
 */
public class ObtainParameter{
    private int n;
    private int k;
    private double[] p;
    private List<Double> detap = new ArrayList<>();
    private double pre_p ;
    private double ave_p;
    private double[] Qa;
    private double[] Qb;
    private double[] R;
    private int left;
    private int right;
    private List<Double> list_window = new ArrayList<>();

    private int normalbit;

    public ObtainParameter(){

    }
    public List<Double> getList_window() {
        return list_window;
    }

    public void setList_window(double rate) {
        this.list_window.add(rate);
    }

    private int newrulebit;

    public int getNormalbit() {
        return normalbit;
    }

    public void setNormalbit(int normalbit) {
        this.normalbit = normalbit;
    }
    public void addNormalbit(int normalbit) {
        this.normalbit += normalbit;
    }

    public int getNewrulebit() {
        return newrulebit;
    }

    public void setNewrulebit(int newrulebit) {
        this.newrulebit = newrulebit;
    }
    public void addNewrulebit(int newrulebit) {
        this.newrulebit += newrulebit;
    }

    //保存所有的p
    public List<Double> list = new ArrayList<>();


    //权值分布情况
    private int[] distribution;

    public int[] getDistribution(short[] w) {
        for(int i=0;i<distribution.length;i++)
            distribution[i]=0;
        for(int i=0;i<w.length;i++){
            if(w[i]>right || w[i]<left){
                throw new InputUnmatchException();
            }
            distribution[w[i]-left]++;
        }
        return distribution;
    }
    public String getDistributionString(){
        if(distribution.length==0)
            throw new InputUnmatchException();
        StringBuffer stringBuffer = new StringBuffer();
        for(int i=0;i<right-left+1;i++){
            stringBuffer.append("权值"+(i-right)+"的数量是"+distribution[i]+"\n");
        }
        return stringBuffer.toString();
    }

    public void setDistribution(int[] distribution) {
        this.distribution = distribution;
    }

    public ObtainParameter(int n, int k, int left, int right){
        this.n = n;
        this.k = k;
        p = new double[k];
        Qa = new double[k];
        Qb = new double[k];
        R = new double[k];

        this.left = left;
        this.right = right;
        distribution = new int[right-left+1];

        //交换参数
        normalbit = 0;
        newrulebit = 0;
    }

    public double[] getQa(short[] wa) {
        if(wa.length!=n*k ) throw new InputUnmatchException();

        for(int j=0;j<k;j++){
            double temp=0;
            for(int i=0;i<n;i++){
                temp += wa[j*n+i]*wa[j*n+i];
            }
            Qa[j] = temp/k;
        }
        return Qa;
    }

    public void setQa(double[] qa) {
        Qa = qa;
    }

    public double[] getQb(short[] wa) {
        if(wa.length!=n*k ) throw new InputUnmatchException();

        for(int j=0;j<k;j++){
            double temp=0;
            for(int i=0;i<n;i++){
                temp += wa[j*n+i]*wa[j*n+i];
            }
            Qb[j] = temp/k;
        }
        return Qb;
    }

    public void setQb(double[] qb) {
        Qb = qb;
    }

    public double[] getR(short[] wa,short[] wb) {
        if(wa.length!=n*k ) throw new InputUnmatchException();

        for(int j=0;j<k;j++){
            double temp=0;
            for(int i=0;i<n;i++){
                temp += wa[j*n+i]*wb[j*n+i];
            }
            R[j] = temp/k;
        }
        return R;
    }

    public void setR(double[] r) {
        R = r;
    }

    public double[] getP() {
        for(int i=0;i<k;i++){
            p[i]=R[i]/(Math.sqrt(Qa[i])*Math.sqrt(Qb[i]));
        }
        BigDecimal bg = new BigDecimal(p[0]);
        list.add(bg.setScale(8, BigDecimal.ROUND_HALF_UP).doubleValue());
        return p;
    }

    public void setP(double[] p) {
        this.p = p;
    }

    public double getAve_p() {
        double temp = 0.0;
        for(int i=0;i<k;i++){
            temp+=p[i];
        }
        ave_p = temp/k;
        if(detap.size()==0){
            detap.add(ave_p);
        }else {
            detap.add(Math.abs(pre_p-ave_p));
        }
        pre_p = Math.abs(ave_p);
        return ave_p;
    }
    public List<Double> getDetap(){
        return detap;
    }

    public void setAve_p(double ave_p) {
        this.ave_p = ave_p;
    }
}

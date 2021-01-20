package utils.Chaos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author : wangxuan
 * @date : 15:49 2020/11/3 0003
 */
public class QueueWithGeo {

    private int K;
    private int L;
    private int N;
    private int[] l_num;
    private int min = Integer.MAX_VALUE;
    private List<int[]> lists = new ArrayList<>();

    public QueueWithGeo(int L,int k,int N){
        l_num = new int[L+1];
        this.L = L;
        this.N = N;
        K = k;
    }
    public short[] getSqueue(short[] weight){
        short[] queue = new short[N*K];
        if(weight.length%K!=0)
            return null;
        int N = weight.length/K;
        int[][] left_right = new int[2][L+1];
        Random random = new Random();
        for(int i=0;i<K;i++){
            lists.clear();
            int r = random.nextInt()%2;
            short[] nums = getCount(Arrays.copyOfRange(weight,i*N,(i+1)*N));
            GetDistribute(nums);
            for(int j=0;j<L+1;j++){
                left_right[0][j] = (nums[j]+lists.get(0)[j])/2;
                left_right[1][j] = nums[j]-left_right[0][j];
            }
            //产生序列
            for(int j=0;j<N;j++){
                int w = weight[j+i*N];
                int r1 = random.nextInt()%2;
                if(w>0){
                    if(left_right[0][w]!=0){
                        if(left_right[1][w]!=0 && Math.abs(r1)==0){
                            queue[j+i*N]=-1;
                            left_right[1][w]--;
                        }else {
                            queue[j+i*N]=1;
                            left_right[0][w]--;
                        }
                    }else {
                        queue[j+i*N]=-1;
                        left_right[1][w]--;
                    }
                }
                if(w<0){
                    if(left_right[0][-w]!=0){
                        queue[j+i*N]=-1;
                        left_right[0][-w]--;
                    }else {
                        queue[j+i*N]=1;
                        left_right[1][-w]--;
                    }
                }
                if(w==0)
                    queue[j+i*N]= (short) (Math.abs(r1)==0?1:-1);
                if(Math.abs(r)==0) queue[j+i*N]= (short) -queue[j+i*N];
            }
        }
        for(int i=0;i<K;i++){
            int sum = test(Arrays.copyOfRange(weight,i*N,(i+1)*N), Arrays.copyOfRange(queue,i*N,(i+1)*N));
            sum = sum;
        }
        return queue;
    }
    public short[] getCount(short[] nums){
        short[] count = new short[L+1];
        for(int i=0;i<nums.length;i++){
            count[Math.abs(nums[i])]++;
        }
        return count;
    }
    //获取单个隐含层节点权值分布
    public void GetDistribute(short[] nums){
        min = Integer.MAX_VALUE;
        l_num[0]=nums[0];
        dfs(nums,N/L*(L+1)*L/2,1,0);
    }

    private void dfs(short[] nums,int H,int pos,int sum){
        if(pos==nums.length){
            if(Math.abs(sum-H)<min){
                min = Math.abs(sum-H);
                if(lists.size()>0)
                    lists.remove(lists.size()-1);
                lists.add(l_num.clone());
            }
            return;
        }
        if(nums[pos]==0){
            l_num[pos]=nums[pos];
            dfs(nums,H,pos+1,sum);
            return;
        }
        int tmp;
        for(int i=0;i<=nums[pos];i++){
            tmp=(nums[pos]-2*i)*pos;
            sum+=tmp;
            l_num[pos] = nums[pos]-2*i;
            dfs(nums,H,pos+1,sum);
            sum-=tmp;
        }
    }
    public int test(short[] weight,short[] input){
        int sum = 0;
        for(int i=0;i<weight.length;i++){
            sum+=weight[i]*input[i];
        }
        return sum;
    }
}

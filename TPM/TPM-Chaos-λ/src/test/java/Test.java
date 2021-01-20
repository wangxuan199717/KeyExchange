import java.util.ArrayList;
import java.util.List;

/**
 * @author : wangxuan
 * @date : 14:38 2020/11/3 0003
 */
public class Test {
    public static void main(String[] args) {
        int[] nums = new int[]{1,4,2,2,2};
        int H = (int) Math.sqrt(nums.length);
        l_num = new int[nums.length];
        l_num[0] = nums[0];
        dfs(nums,20,1,0);
        return;
    }

    public static int[] l_num;
    public static double rate = 0.8;
    public static boolean flag = false;
    public static int min = Integer.MAX_VALUE;
    public static List<int[]> lists = new ArrayList<>();

    public static void dfs(int[] nums,int H,int pos,int sum){
        if(pos==nums.length){
            if(Math.abs(sum-H)<min){
                lists.add(l_num.clone());
                min = Math.abs(sum-H);
            }
            if(Math.abs(sum-H)/H>rate){
                flag=true;
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
            if(flag) break;
            tmp=(nums[pos]-2*i)*pos;
            sum+=tmp;
            l_num[pos] = nums[pos]-2*i;
            dfs(nums,H,pos+1,sum);
            sum-=tmp;
        }
    }
}

import Chaos_System.Chaos;
import Chaos_System.Logistical;
import Encryption.Encrypt;
import ImageUtils.Image;
import java.util.List;

/**
 * @author : wangxuan
 * @date : 10:26 2020/6/10 0010
 */
public class Main1 {
    public static final String path = "C:\\Users\\Administrator\\Desktop\\TPM\\ImageEncryption\\resource\\";
    public static int width;
    public static int height;

    public static void main(String[] args) throws Exception {

        //加密使用到的一维混沌参数u=3.98 x=0.5
        Chaos<Double> chaos = new Logistical(3.98,0.5);
        Image image = new Image(path+"baboon.jpeg");

        width = image.getImageInfo().getWidth();
        height = image.getImageInfo().getHeight();

        //获取到混沌序列
        List<Double> squeue = chaos.GetSqueue(width*height);
        //将混沌序列转换为0~255的秘钥
        char[][] key =((Logistical) chaos).convey(squeue,width,height);
        //异或产生结果
        char[][][] results = Encrypt.andor(image.getRgb24(),key,width,height);
        //结果写回文件，加密完成
        image.writeImage(width,height,results,path+"baboon_encrypt.bmp");

        //解密过程
        char[][][] decrypt = Encrypt.andor(results,key,width,height);
        image.writeImage(width,height,decrypt,path+"baboon_dencrypt.bmp");

    }
}

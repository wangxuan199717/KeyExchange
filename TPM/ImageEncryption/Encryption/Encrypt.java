package Encryption;

/**
 * @author : wangxuan
 * @date : 13:33 2020/6/10 0010
 */
public class Encrypt {

    public static char[][][] andor(char[][][] image,char[][] key,int width,int height){

        char[][][] result = new char[height][width][3];
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                result[i][j][0] = (char) ((image[i][j][0] ^ key[i][j])&0x000000ff);
                result[i][j][1] = (char) ((image[i][j][1] ^ key[i][j])&0x000000ff);
                result[i][j][2] = (char) ((image[i][j][2] ^ key[i][j])&0x000000ff);
            }
        }
        return result;
    }
    public static char[][][] decrypt(char[][] key,char[][][]image,int width,int height){
        char[][][] result = new char[height][width][3];
        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                result[i][j][0] = (char) ((image[i][j][0] ^ key[i][j])&0x000000ff);
                result[i][j][1] = (char) ((image[i][j][1] ^ key[i][j])&0x000000ff);
                result[i][j][2] = (char) ((image[i][j][2] ^ key[i][j])&0x000000ff);
            }
        }
        return result;
    }
}

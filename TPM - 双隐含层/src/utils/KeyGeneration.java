package utils;

/**
 * @author : wangxuan
 * @date : 11:11 2020/11/30 0030
 */

//将同步得到的数字转换成秘钥
public class KeyGeneration {
    public static String generateKey(short[] key,int L){
        String s = convey(key,L);
        return base64(s);
    }
    private static String convey(short[] key,int L){
        if(L>=127) return null;
        int range = L*2+1;
        int num = 0;
        int tmp = 1;
        while(tmp<range){
            num++;
            tmp=tmp<<1;
        }
        StringBuilder stringBuilder = new StringBuilder();
        char c = 0x00;
        int pos = 0;
        for(int i=0;i<key.length;i++){
            int num1 = key[i]+L;
            for(int j=0;j<num;j++){
                c = (char) (c|num1&0x00000001);
                c = (char) (c<<1);
                num1=num1>>1;
                pos++;
                if(pos==8){
                    stringBuilder.append(c);
                    pos=0;
                    c=0x00;
                }
            }
        }
        return stringBuilder.toString();
    }
    private static String base64(String s){
        char[] list = {
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
                'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
                'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
                'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
        };

        StringBuffer stringBuffer = new StringBuffer();

        for(int i=0;i<s.length()-3;i+=3){
            char s1 = (char) ((s.charAt(i) & 0xfc) >>2);
            char s2 = (char) ((s.charAt(i) & 0x03) <<4 | (s.charAt(i+1) & 0xf0) >>4);
            char s3 = (char) ((s.charAt(i+1) & 0x0f) <<2 | (s.charAt(i+2) & 0xc0) >>6);
            char s4 = (char) (s.charAt(i+2) & 0x3f);
            stringBuffer.append(list[s1]);
            stringBuffer.append(list[s2]);
            stringBuffer.append(list[s3]);
            stringBuffer.append(list[s4]);
        }

        if(s.length()%3 !=0){
            if(s.length()%3==2){
                char s1 = (char) ((s.charAt(s.length()-2) & 0xfc) >>2);
                char s2 = (char) ((s.charAt(s.length()-2) & 0x03) <<4 | (s.charAt(s.length()-1) & 0xf0) >> 4);
                char s3 = (char) ((s.charAt(s.length()-1) & 0x0f )<<2);
                stringBuffer.append(list[s1]);
                stringBuffer.append(list[s2]);
                stringBuffer.append(list[s3]);
                stringBuffer.append("=");
            }else {
                char s1 = (char) ((s.charAt(s.length()-1) & 0xfc) >>2);
                char s2 = (char) ((s.charAt(s.length()-1) & 0x03)<<4);
                stringBuffer.append(list[s1]);
                stringBuffer.append(list[s2]);
                stringBuffer.append("==");
            }
        }
        return stringBuffer.toString();
    }
}

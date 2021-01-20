package ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

/**
 * @author : wangxuan
 * @date : 10:34 2020/6/10 0010
 */
public class Image {
    private String path;
    private char[][][] rgb24;
    private ImageInfo imageInfo;

    public char[][][] getRgb24() {
        return rgb24;
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    public void setImageInfo(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }

    public Image(String path) throws Exception {
        this.path = path;
        imageInfo = loadTestImage();
    }
    private ImageInfo loadTestImage() throws Exception{
        File img = new File(path);
        if (!img.exists()) {
            return null;
        }
        BufferedImage image = null;
        try {
            image = ImageIO.read(img);
        } catch (Exception e) {
            if(image == null){
                img = new File(path);
                image = ImageIO.read(img);
            }
            if(image == null){
                System.out.println("转换图片失败,源图片路径："+path+ "；原因：该图片无法转换成rgb");
                return null;
            }
        }
        if (image == null) {
            return null;
        }

        final int width = image.getWidth();
        final int height = image.getHeight();
        int[] pix = new int[width * height];
        PixelGrabber pg = new PixelGrabber(image, 0, 0, width, height, pix, 0, width);
        try {
            if (!pg.grabPixels()) {
                return null;
            }
        } catch (InterruptedException e) {
            System.out.println("转换图片失败,源图片路径："+path+ "；原因：该图片无法转换成rgb.");
        }

        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setWidth(width);
        imageInfo.setHeight(height);
        rgb24 = new char[height][width][3];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int idx = width * i + j;
                int color = pix[idx]; //获取像素
                int red = ((color & 0x00FF0000) >> 16);
                int green = ((color & 0x0000FF00) >> 8);
                int blue = color & 0x000000FF;
                rgb24[i][j][0] = (char) red;
                rgb24[i][j][1] = (char) green;
                rgb24[i][j][2] = (char) blue;
            }
        }
        imageInfo.setImage(rgb24);
        return imageInfo;
    }

    public void writeImage(int width,int height,char[][][] image,String path) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);

        for(int i=0;i<height;i++){
            for(int j=0;j<width;j++){
                int tmp=0;
                tmp += (image[i][j][0] &0x000000ff)<<16;
                tmp += (image[i][j][1] &0x000000ff)<<8;
                tmp += (image[i][j][2] &0x000000ff);
                bufferedImage.setRGB(j,i,tmp);
            }
        }
        File file = new File(path);
        ImageIO.write(bufferedImage,"BMP",file);
    }
}

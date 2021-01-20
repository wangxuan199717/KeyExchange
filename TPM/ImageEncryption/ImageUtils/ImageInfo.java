package ImageUtils;

/**
 * @author : wangxuan
 * @date : 13:18 2020/6/10 0010
 */
public class ImageInfo {
        private char[][][] image;
        private int width;
        private int height;
        private float score;

        public float getScore() {
            return score;
        }

        public void setScore(float score) {
            this.score = score;
        }

        public char[][][] getImage() {
            return image;
        }

        public void setImage(char[][][] image) {
            this.image = image;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public void setHeight(int height) {
            this.height = height;
        }
}

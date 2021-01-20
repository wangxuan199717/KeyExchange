package utils;

public class Random {

    private static long seed;

    public Random() {
    }

    public static short getInt(int leftBound, int rightBound) {
        return (short) new java.util.Random().ints(1, leftBound, rightBound + 1).findFirst().getAsInt();
    }

    public static short[] getInts(int n, int leftBound, int rightBound) {
        short[] shorts = new short[n];
        for(int i = 0; i < n; i++)
            shorts[i] = getInt(leftBound, rightBound);
        return shorts;
    }

    public static void setSeed(long seed) {
        Random.seed = seed;
    }

    public static long getSeed() {
        return seed;
    }
}

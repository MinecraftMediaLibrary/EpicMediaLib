package com.github.pulsebeat02.video.dither;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;

public class OrderedDithering implements AbstractDitherHolder {

    /**
     * Performs Ordered Dithering with a selection of
     * matrices to choose from.
     *
     * @author PulseBeat_02
     */
    private static final byte[] COLOR_MAP;

    private final static float[][] bayerMatrixTwo;
    private final static float[][] bayerMatrixFour;
    private final static float[][] bayerMatrixEight;

    static {

        COLOR_MAP = StaticDitherInitialization.COLOR_MAP;

        /*

        2 by 2 Bayer Ordered Dithering

        0   2
        3   1  (1/4)

        */

        bayerMatrixTwo = new float[][]{
                {1f, 3f},
                {4f, 2f},
        };

        /*

        4 by 4 Bayer Ordered Dithering

        1  9  3  11
        13 5  15  7
        4  12  2  10
        16 8  14  6   (1/16)

         */

        bayerMatrixFour = new float[][]{
                {1f, 9f, 3f, 11f},
                {13f, 5f, 15f, 7f},
                {4f, 12f, 2f, 10f},
                {16f, 8f, 14f, 6f}
        };

        /*

        8 by 8 Bayer Ordered Dithering

        1  49  13  61  4  52  16  64
        33 17  45  29  36  20  48  32
        9  57  5  53  12  60  8  56
        41  25  37  21  44  28  40  24
        3  51  15  63  2  50  14  62
        35  19  47  31  34  18  46  30
        11  59  7  55  10  58  6  54
        43  27  39  23  42  26  38  22   (1/64)

         */

        bayerMatrixEight = new float[][]{
                {1f, 49f, 13f, 61f, 4f, 52f, 16f, 64f},
                {33f, 17f, 45f, 29f, 36f, 20f, 48f, 32f},
                {9f, 57f, 5f, 53f, 12f, 60f, 8f, 56f},
                {41f, 25f, 37f, 21f, 44f, 28f, 40f, 24f},
                {3f, 51f, 15f, 63f, 2f, 50f, 14f, 62f},
                {35f, 19f, 47f, 31f, 34f, 18f, 46f, 30f},
                {11f, 59f, 7f, 55f, 10f, 58f, 6f, 54f},
                {43f, 27f, 39f, 23f, 42f, 26f, 38f, 22f}
        };

    }

    private float[][] matrix;
    private final float r;
    private float multiplicative;
    private int n;

    public OrderedDithering(@NotNull final DitherType type) {
        switch (type) {
            case ModeTwo:
                matrix = bayerMatrixTwo;
                n = 2;
                multiplicative = 0.25f;
                break;
            case ModeFour:
                matrix = bayerMatrixFour;
                n = 4;
                multiplicative = 0.0625f;
                break;
            case ModeEight:
                matrix = bayerMatrixEight;
                n = 8;
                multiplicative = 0.015625f;
                break;
        }
        this.r = 255f / (n * n);
        convertToFloat();
    }

    public int getBestColorNormal(int rgb) {
        return MinecraftMapPalette.getColor(getBestColor(rgb >> 16 & 0xFF, rgb >> 8 & 0xFF, rgb & 0xFF)).getRGB();
    }

    public byte getBestColor(int rgb) {
        return COLOR_MAP[(rgb >> 16 & 0xFF) >> 1 << 14 | (rgb >> 8 & 0xFF) >> 1 << 7 | (rgb & 0xFF) >> 1];
    }

    public byte getBestColor(int red, int green, int blue) {
        return COLOR_MAP[red >> 1 << 14 | green >> 1 << 7 | blue >> 1];
    }

    public void convertToFloat() {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = matrix[i][j] * multiplicative - 0.5f;
            }
        }
    }

    @Override
    public void dither(int[] buffer, int width) {
        int height = buffer.length / width;
        for (int y = 0; y < height; y++) {
            int yIndex = y * width;
            for (int x = 0; x < width; x++) {
                int index = yIndex + x;
                int color = buffer[index];
                buffer[index] = getBestColorNormal((int)(color + r * (matrix[x % n][y % n])));
            }
        }
    }

    @Override
    public ByteBuffer ditherIntoMinecraft(int[] buffer, int width) {
        int height = buffer.length / width;
        ByteBuffer data = ByteBuffer.allocate(buffer.length);
        for (int y = 0; y < height; y++) {
            int yIndex = y * width;
            for (int x = 0; x < width; x++) {
                int index = yIndex + x;
                int color = buffer[index];
                data.put(getBestColor((int)(color + r * (matrix[x % n][y % n]))));
            }
        }
        return data;
    }

    public enum DitherType {

        ModeTwo("Two Dimensional"),
        ModeFour("Four Dimensional"),
        ModeEight("Eight Dimensional");

        private final String name;

        DitherType(@NotNull final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

    public static byte[] getColorMap() {
        return COLOR_MAP;
    }

    public static float[][] getBayerMatrixTwo() {
        return bayerMatrixTwo;
    }

    public static float[][] getBayerMatrixFour() {
        return bayerMatrixFour;
    }

    public static float[][] getBayerMatrixEight() {
        return bayerMatrixEight;
    }

    public float[][] getMatrix() {
        return matrix;
    }

    public float getR() {
        return r;
    }

    public float getMultiplicative() {
        return multiplicative;
    }

    public int getN() {
        return n;
    }

}
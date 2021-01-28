package com.github.pulsebeat02.video.dither;

import java.nio.ByteBuffer;

public class FilterLiteDither implements AbstractDitherHolder {

    /**
     * Performs Filter Lite Dithering at a more optimized
     * pace while giving similar results to Floyd Steinberg Dithering.
     *
     * @author PulseBeat_02
     */

    private static final byte[] COLOR_MAP;
    private static final int[] FULL_COLOR_MAP;

    static {
        COLOR_MAP = StaticDitherInitialization.COLOR_MAP;
        FULL_COLOR_MAP = StaticDitherInitialization.FULL_COLOR_MAP;
    }

    @Override
    public void dither(int[] buffer, int width) {
        int height = buffer.length / width;
        int widthMinus = width - 1;
        int heightMinus = height - 1;
        int[][] dither_buffer = new int[2][width + width << 1];

        /*

        Simple Sierra 2-4A Dithering (Filter Lite)


            *  2
        1  1       (1/4)

        When Jagged Matrix is Multiplied:

              *  2/4
        1/4 1/4

         */

        for (int y = 0; y < height; ++y) {
            boolean hasNextY = y < heightMinus;
            int yIndex = y * width;
            if ((y & 0x1) == 0) {
                int bufferIndex = 0;
                int[] buf1 = dither_buffer[0];
                int[] buf2 = dither_buffer[1];
                for (int x = 0; x < width; ++x) {
                    int index = yIndex + x;
                    int rgb = buffer[index];
                    int red = rgb >> 16 & 0xFF;
                    int green = rgb >> 8 & 0xFF;
                    int blue = rgb & 0xFF;
                    red = (red += buf1[bufferIndex++]) > 255 ? 255 : red < 0 ? 0 : red;
                    green = (green += buf1[bufferIndex++]) > 255 ? 255 : green < 0 ? 0 : green;
                    blue = (blue += buf1[bufferIndex++]) > 255 ? 255 : blue < 0 ? 0 : blue;
                    int closest = getBestFullColor(red, green, blue);
                    int delta_r = red - (closest >> 16 & 0xFF);
                    int delta_g = green - (closest >> 8 & 0xFF);
                    int delta_b = blue - (closest & 0xFF);
                    if (x < widthMinus) {
                        buf1[bufferIndex] = delta_r >> 1;
                        buf1[bufferIndex + 1] = delta_g >> 1;
                        buf1[bufferIndex + 2] = delta_b >> 1;
                    }
                    if (hasNextY) {
                        if (x > 0) {
                            buf2[bufferIndex - 6] = delta_r >> 2;
                            buf2[bufferIndex - 5] = delta_g >> 2;
                            buf2[bufferIndex - 4] = delta_b >> 2;
                        }
                        buf2[bufferIndex - 3] = delta_r >> 2;
                        buf2[bufferIndex - 2] = delta_g >> 2;
                        buf2[bufferIndex - 1] = delta_b >> 2;
                    }
                    buffer[index] = closest;
                }
            } else {
                int bufferIndex = width + (width << 1) - 1;
                int[] buf1 = dither_buffer[1];
                int[] buf2 = dither_buffer[0];
                for (int x = width - 1; x >= 0; --x) {
                    int index = yIndex + x;
                    int rgb = buffer[index];
                    int red = rgb >> 16 & 0xFF;
                    int green = rgb >> 8 & 0xFF;
                    int blue = rgb & 0xFF;
                    blue = (blue += buf1[bufferIndex--]) > 255 ? 255 : blue < 0 ? 0 : blue;
                    green = (green += buf1[bufferIndex--]) > 255 ? 255 : green < 0 ? 0 : green;
                    red = (red += buf1[bufferIndex--]) > 255 ? 255 : red < 0 ? 0 : red;
                    int closest = getBestFullColor(red, green, blue);
                    int delta_r = red - (closest >> 16 & 0xFF);
                    int delta_g = green - (closest >> 8 & 0xFF);
                    int delta_b = blue - (closest & 0xFF);
                    if (x > 0) {
                        buf1[bufferIndex] = delta_b >> 1;
                        buf1[bufferIndex - 1] = delta_g >> 1;
                        buf1[bufferIndex - 2] = delta_r >> 1;
                    }
                    if (hasNextY) {
                        if (x < widthMinus) {
                            buf2[bufferIndex + 6] = delta_b >> 2;
                            buf2[bufferIndex + 5] = delta_g >> 2;
                            buf2[bufferIndex + 4] = delta_r >> 2;
                        }
                        buf2[bufferIndex + 3] = delta_b >> 2;
                        buf2[bufferIndex + 2] = delta_g >> 2;
                        buf2[bufferIndex + 1] = delta_r >> 2;
                    }
                    buffer[index] = closest;
                }
            }
        }
    }

    @Override
    public ByteBuffer ditherIntoMinecraft(int[] buffer, int width) {
        int height = buffer.length / width;
        int widthMinus = width - 1;
        int heightMinus = height - 1;
        int[][] dither_buffer = new int[2][width + width << 1];
        ByteBuffer data = ByteBuffer.allocate(buffer.length);
        for (int y = 0; y < height; ++y) {
            boolean hasNextY = y < heightMinus;
            int yIndex = y * width;
            if ((y & 0x1) == 0) {
                int bufferIndex = 0;
                int[] buf1 = dither_buffer[0];
                int[] buf2 = dither_buffer[1];
                for (int x = 0; x < width; ++x) {
                    int index = yIndex + x;
                    int rgb = buffer[index];
                    int red = rgb >> 16 & 0xFF;
                    int green = rgb >> 8 & 0xFF;
                    int blue = rgb & 0xFF;
                    red = (red += buf1[bufferIndex++]) > 255 ? 255 : red < 0 ? 0 : red;
                    green = (green += buf1[bufferIndex++]) > 255 ? 255 : green < 0 ? 0 : green;
                    blue = (blue += buf1[bufferIndex++]) > 255 ? 255 : blue < 0 ? 0 : blue;
                    int closest = getBestFullColor(red, green, blue);
                    int delta_r = red - (closest >> 16 & 0xFF);
                    int delta_g = green - (closest >> 8 & 0xFF);
                    int delta_b = blue - (closest & 0xFF);
                    if (x < widthMinus) {
                        buf1[bufferIndex] = delta_r >> 1;
                        buf1[bufferIndex + 1] = delta_g >> 1;
                        buf1[bufferIndex + 2] = delta_b >> 1;
                    }
                    if (hasNextY) {
                        if (x > 0) {
                            buf2[bufferIndex - 6] = delta_r >> 2;
                            buf2[bufferIndex - 5] = delta_g >> 2;
                            buf2[bufferIndex - 4] = delta_b >> 2;
                        }
                        buf2[bufferIndex - 3] = delta_r >> 2;
                        buf2[bufferIndex - 2] = delta_g >> 2;
                        buf2[bufferIndex - 1] = delta_b >> 2;
                    }
                    data.put(index, getBestColor(closest));
                }
            } else {
                int bufferIndex = width + (width << 1) - 1;
                int[] buf1 = dither_buffer[1];
                int[] buf2 = dither_buffer[0];
                for (int x = width - 1; x >= 0; --x) {
                    int index = yIndex + x;
                    int rgb = buffer[index];
                    int red = rgb >> 16 & 0xFF;
                    int green = rgb >> 8 & 0xFF;
                    int blue = rgb & 0xFF;
                    blue = (blue += buf1[bufferIndex--]) > 255 ? 255 : blue < 0 ? 0 : blue;
                    green = (green += buf1[bufferIndex--]) > 255 ? 255 : green < 0 ? 0 : green;
                    red = (red += buf1[bufferIndex--]) > 255 ? 255 : red < 0 ? 0 : red;
                    int closest = getBestFullColor(red, green, blue);
                    int delta_r = red - (closest >> 16 & 0xFF);
                    int delta_g = green - (closest >> 8 & 0xFF);
                    int delta_b = blue - (closest & 0xFF);
                    if (x > 0) {
                        buf1[bufferIndex] = delta_b >> 1;
                        buf1[bufferIndex - 1] = delta_g >> 1;
                        buf1[bufferIndex - 2] = delta_r >> 1;
                    }
                    if (hasNextY) {
                        if (x < widthMinus) {
                            buf2[bufferIndex + 6] = delta_b >> 2;
                            buf2[bufferIndex + 5] = delta_g >> 2;
                            buf2[bufferIndex + 4] = delta_r >> 2;
                        }
                        buf2[bufferIndex + 3] = delta_b >> 2;
                        buf2[bufferIndex + 2] = delta_g >> 2;
                        buf2[bufferIndex + 1] = delta_r >> 2;
                    }
                    data.put(index, getBestColor(closest));
                }
            }
        }
        return data;
    }

    public int getBestFullColor(int red, int green, int blue) {
        return FULL_COLOR_MAP[red >> 1 << 14 | green >> 1 << 7 | blue >> 1];
    }

    public byte getBestColor(int rgb) {
        return COLOR_MAP[(rgb >> 16 & 0xFF) >> 1 << 14 | (rgb >> 8 & 0xFF) >> 1 << 7 | (rgb & 0xFF) >> 1];
    }

    public static byte[] getColorMap() {
        return COLOR_MAP;
    }

    public static int[] getFullColorMap() {
        return FULL_COLOR_MAP;
    }

}
package io.github.pulsebeat02.ezmediacore.dither.algorithm.error;

import io.github.pulsebeat02.ezmediacore.dither.algorithm.ForeignDitherAlgorithm;
import io.github.pulsebeat02.ezmediacore.dither.palette.ColorPalette;
import io.github.pulsebeat02.ezmediacore.util.graphics.DitherUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class StevensonArceDither extends ForeignDitherAlgorithm {

  public StevensonArceDither(final ColorPalette palette) {
    super(palette, false);
  }

  @Override
  public void dither(final int[] buffer, final int width) {
    final ColorPalette palette = this.getPalette();
    final int height = buffer.length / width;
    final int widthMinus = width - 1;
    final int heightMinus = height - 1;
    final int[][] ditherBuffer = new int[3][width + width << 1];
    for (int y = 0; y < height; y++) {
      final boolean hasNextY = y < heightMinus;
      final int yIndex = y * width;
      if ((y & 0x1) == 0) {
        int bufferIndex = 0;
        final int[] buf1 = ditherBuffer[0];
        final int[] buf2 = ditherBuffer[1];
        final int[] buf3 = ditherBuffer[2];
        for (int x = 0; x < width; x++) {
          final boolean hasNextX = x < widthMinus;
          final int index = yIndex + x;
          final int rgb = buffer[index];
          int red = rgb >> 16 & 0xFF;
          int green = rgb >> 8 & 0xFF;
          int blue = rgb & 0xFF;
          red = (red += buf1[bufferIndex++]) > 255 ? 255 : Math.max(red, 0);
          green = (green += buf1[bufferIndex++]) > 255 ? 255 : Math.max(green, 0);
          blue = (blue += buf1[bufferIndex++]) > 255 ? 255 : Math.max(blue, 0);
          final int closest = DitherUtils.getBestFullColor(palette, red, green, blue);
          final int delta_r = red - (closest >> 16 & 0xFF);
          final int delta_g = green - (closest >> 8 & 0xFF);
          final int delta_b = blue - (closest & 0xFF);
          if (hasNextX) {
            buf1[bufferIndex] = (delta_r * 32) / 200;
            buf1[bufferIndex + 1] = (delta_g * 32) / 200;
            buf1[bufferIndex + 2] = (delta_b * 32) / 200;
          }
          if (hasNextY) {
            if (x > 1) {
              buf2[bufferIndex - 6] = (delta_r * 12) / 200;
              buf2[bufferIndex - 5] = (delta_g * 12) / 200;
              buf2[bufferIndex - 4] = (delta_b * 12) / 200;
            }
            if (x > 0) {
              buf2[bufferIndex - 3] = (delta_r * 26) / 200;
              buf2[bufferIndex - 2] = (delta_g * 26) / 200;
              buf2[bufferIndex - 1] = (delta_b * 26) / 200;
            }
            buf2[bufferIndex] = (delta_r * 30) / 200;
            buf2[bufferIndex + 1] = (delta_g * 30) / 200;
            buf2[bufferIndex + 2] = (delta_b * 30) / 200;
            if (x < widthMinus) {
              buf2[bufferIndex + 3] = (delta_r * 16) / 200;
              buf2[bufferIndex + 4] = (delta_g * 16) / 200;
              buf2[bufferIndex + 5] = (delta_b * 16) / 200;
            }
            if (x < width - 2) {
              buf2[bufferIndex + 6] = (delta_r * 12) / 200;
              buf2[bufferIndex + 7] = (delta_g * 12) / 200;
              buf2[bufferIndex + 8] = (delta_b * 12) / 200;
            }
            if (y < height - 2) {
              if (x > 1) {
                buf3[bufferIndex - 6] = (delta_r * 5) / 200;
                buf3[bufferIndex - 5] = (delta_g * 5) / 200;
                buf3[bufferIndex - 4] = (delta_b * 5) / 200;
              }
              if (x > 0) {
                buf3[bufferIndex - 3] = (delta_r * 12) / 200;
                buf3[bufferIndex - 2] = (delta_g * 12) / 200;
                buf3[bufferIndex - 1] = (delta_b * 12) / 200;
              }
              buf3[bufferIndex] = (delta_r * 26) / 200;
              buf3[bufferIndex + 1] = (delta_g * 26) / 200;
              buf3[bufferIndex + 2] = (delta_b * 26) / 200;
              if (x < widthMinus) {
                buf3[bufferIndex + 3] = (delta_r * 12) / 200;
                buf3[bufferIndex + 4] = (delta_g * 12) / 200;
                buf3[bufferIndex + 5] = (delta_b * 12) / 200;
              }
              if (x < width - 2) {
                buf3[bufferIndex + 6] = (delta_r * 5) / 200;
                buf3[bufferIndex + 7] = (delta_g * 5) / 200;
                buf3[bufferIndex + 8] = (delta_b * 5) / 200;
              }
            }
          }
          buffer[index] = closest;
        }
      } else {
        int bufferIndex = width + (width << 1) - 1;
        final int[] buf1 = ditherBuffer[1];
        final int[] buf2 = ditherBuffer[0];
        final int[] buf3 = ditherBuffer[2];
        for (int x = width - 1; x >= 0; x--) {
          final boolean hasNextX = x > 0;
          final int index = yIndex + x;
          final int rgb = buffer[index];
          int red = rgb >> 16 & 0xFF;
          int green = rgb >> 8 & 0xFF;
          int blue = rgb & 0xFF;
          blue = (blue += buf1[bufferIndex--]) > 255 ? 255 : Math.max(blue, 0);
          green = (green += buf1[bufferIndex--]) > 255 ? 255 : Math.max(green, 0);
          red = (red += buf1[bufferIndex--]) > 255 ? 255 : Math.max(red, 0);
          final int closest = DitherUtils.getBestFullColor(palette, red, green, blue);
          final int delta_r = red - (closest >> 16 & 0xFF);
          final int delta_g = green - (closest >> 8 & 0xFF);
          final int delta_b = blue - (closest & 0xFF);
          if (hasNextX) {
            buf1[bufferIndex] = (delta_b * 32) / 200;
            buf1[bufferIndex - 1] = (delta_g * 32) / 200;
            buf1[bufferIndex - 2] = (delta_r * 32) / 200;
          }
          if (hasNextY) {
            if (x < widthMinus) {
              buf2[bufferIndex + 6] = (delta_b * 12) / 200;
              buf2[bufferIndex + 5] = (delta_g * 12) / 200;
              buf2[bufferIndex + 4] = (delta_r * 12) / 200;
            }
            if (x < width - 2) {
              buf2[bufferIndex + 9] = (delta_b * 5) / 200;
              buf2[bufferIndex + 8] = (delta_g * 5) / 200;
              buf2[bufferIndex + 7] = (delta_r * 5) / 200;
            }
            buf2[bufferIndex + 3] = (delta_b * 26) / 200;
            buf2[bufferIndex + 2] = (delta_g * 26) / 200;
            buf2[bufferIndex + 1] = (delta_r * 26) / 200;
            buf2[bufferIndex] = (delta_b * 30) / 200;
            buf2[bufferIndex - 1] = (delta_g * 30) / 200;
            buf2[bufferIndex - 2] = (delta_r * 30) / 200;
            if (x > 0) {
              buf2[bufferIndex - 3] = (delta_b * 16) / 200;
              buf2[bufferIndex - 4] = (delta_g * 16) / 200;
              buf2[bufferIndex - 5] = (delta_r * 16) / 200;
            }
            if (x > 1) {
              buf2[bufferIndex - 6] = (delta_b * 12) / 200;
              buf2[bufferIndex - 7] = (delta_g * 12) / 200;
              buf2[bufferIndex - 8] = (delta_r * 12) / 200;
            }
            if (y < height - 2) {
              if (x < widthMinus) {
                buf3[bufferIndex + 6] = (delta_b * 12) / 200;
                buf3[bufferIndex + 5] = (delta_g * 12) / 200;
                buf3[bufferIndex + 4] = (delta_r * 12) / 200;
              }
              if (x < width - 2) {
                buf3[bufferIndex + 9] = (delta_b * 5) / 200;
                buf3[bufferIndex + 8] = (delta_g * 5) / 200;
                buf3[bufferIndex + 7] = (delta_r * 5) / 200;
              }
              buf3[bufferIndex + 3] = (delta_b * 26) / 200;
              buf3[bufferIndex + 2] = (delta_g * 26) / 200;
              buf3[bufferIndex + 1] = (delta_r * 26) / 200;
              buf3[bufferIndex] = (delta_b * 30) / 200;
              buf3[bufferIndex - 1] = (delta_g * 30) / 200;
              buf3[bufferIndex - 2] = (delta_r * 30) / 200;
              if (x > 0) {
                buf3[bufferIndex - 3] = (delta_b * 16) / 200;
                buf3[bufferIndex - 4] = (delta_g * 16) / 200;
                buf3[bufferIndex - 5] = (delta_r * 16) / 200;
              }
              if (x > 1) {
                buf3[bufferIndex - 6] = (delta_b * 12) / 200;
                buf3[bufferIndex - 7] = (delta_g * 12) / 200;
                buf3[bufferIndex - 8] = (delta_r * 12) / 200;
              }
            }
          }
          buffer[index] = closest;
        }
      }
    }
  }

  @Override
  public byte[] standardMinecraftDither(final int[] buffer, final int width) {
    final ColorPalette palette = this.getPalette();
    final int height = buffer.length / width;
    final int widthMinus = width - 1;
    final int heightMinus = height - 1;
    final int[][] ditherBuffer = new int[3][width + (width << 1)];
    final ByteBuf data = Unpooled.buffer(buffer.length);

    for (int y = 0; y < height; y++) {
      final boolean hasNextY = y < heightMinus;
      final int yIndex = y * width;
      if ((y & 0x1) == 0) {
        int bufferIndex = 0;
        final int[] buf1 = ditherBuffer[0];
        final int[] buf2 = ditherBuffer[1];
        final int[] buf3 = ditherBuffer[2];
        for (int x = 0; x < width; x++) {
          final boolean hasNextX = x < widthMinus;
          final int index = yIndex + x;
          final int rgb = buffer[index];
          int red = rgb >> 16 & 0xFF;
          int green = rgb >> 8 & 0xFF;
          int blue = rgb & 0xFF;
          red = (red += buf1[bufferIndex++]) > 255 ? 255 : Math.max(red, 0);
          green = (green += buf1[bufferIndex++]) > 255 ? 255 : Math.max(green, 0);
          blue = (blue += buf1[bufferIndex++]) > 255 ? 255 : Math.max(blue, 0);
          final int closest = DitherUtils.getBestFullColor(palette, red, green, blue);
          final int r = closest >> 16 & 0xFF;
          final int g = closest >> 8 & 0xFF;
          final int b = closest & 0xFF;
          final int delta_r = red - r;
          final int delta_g = green - g;
          final int delta_b = blue - b;
          if (hasNextX) {
            buf1[bufferIndex] = (delta_r * 32) / 200;
            buf1[bufferIndex + 1] = (delta_g * 32) / 200;
            buf1[bufferIndex + 2] = (delta_b * 32) / 200;
          }
          if (hasNextY) {
            if (x > 1) {
              buf2[bufferIndex - 6] = (delta_r * 12) / 200;
              buf2[bufferIndex - 5] = (delta_g * 12) / 200;
              buf2[bufferIndex - 4] = (delta_b * 12) / 200;
            }
            if (x > 0) {
              buf2[bufferIndex - 3] = (delta_r * 26) / 200;
              buf2[bufferIndex - 2] = (delta_g * 26) / 200;
              buf2[bufferIndex - 1] = (delta_b * 26) / 200;
            }
            buf2[bufferIndex] = (delta_r * 30) / 200;
            buf2[bufferIndex + 1] = (delta_g * 30) / 200;
            buf2[bufferIndex + 2] = (delta_b * 30) / 200;
            if (x < widthMinus) {
              buf2[bufferIndex + 3] = (delta_r * 16) / 200;
              buf2[bufferIndex + 4] = (delta_g * 16) / 200;
              buf2[bufferIndex + 5] = (delta_b * 16) / 200;
            }
            if (x < width - 2) {
              buf2[bufferIndex + 6] = (delta_r * 12) / 200;
              buf2[bufferIndex + 7] = (delta_g * 12) / 200;
              buf2[bufferIndex + 8] = (delta_b * 12) / 200;
            }
            if (y < height - 2) {
              if (x > 1) {
                buf3[bufferIndex - 6] = (delta_r * 5) / 200;
                buf3[bufferIndex - 5] = (delta_g * 5) / 200;
                buf3[bufferIndex - 4] = (delta_b * 5) / 200;
              }
              if (x > 0) {
                buf3[bufferIndex - 3] = (delta_r * 12) / 200;
                buf3[bufferIndex - 2] = (delta_g * 12) / 200;
                buf3[bufferIndex - 1] = (delta_b * 12) / 200;
              }
              buf3[bufferIndex] = (delta_r * 26) / 200;
              buf3[bufferIndex + 1] = (delta_g * 26) / 200;
              buf3[bufferIndex + 2] = (delta_b * 26) / 200;
              if (x < widthMinus) {
                buf3[bufferIndex + 3] = (delta_r * 12) / 200;
                buf3[bufferIndex + 4] = (delta_g * 12) / 200;
                buf3[bufferIndex + 5] = (delta_b * 12) / 200;
              }
              if (x < width - 2) {
                buf3[bufferIndex + 6] = (delta_r * 5) / 200;
                buf3[bufferIndex + 7] = (delta_g * 5) / 200;
                buf3[bufferIndex + 8] = (delta_b * 5) / 200;
              }
            }
          }
          data.setByte(index, DitherUtils.getBestColor(palette, r, g, b));
        }
      } else {
        int bufferIndex = width + (width << 1) - 1;
        final int[] buf1 = ditherBuffer[1];
        final int[] buf2 = ditherBuffer[0];
        final int[] buf3 = ditherBuffer[2];
        for (int x = width - 1; x >= 0; x--) {
          final boolean hasNextX = x > 0;
          final int index = yIndex + x;
          final int rgb = buffer[index];
          int red = rgb >> 16 & 0xFF;
          int green = rgb >> 8 & 0xFF;
          int blue = rgb & 0xFF;
          blue = (blue += buf1[bufferIndex--]) > 255 ? 255 : Math.max(blue, 0);
          green = (green += buf1[bufferIndex--]) > 255 ? 255 : Math.max(green, 0);
          red = (red += buf1[bufferIndex--]) > 255 ? 255 : Math.max(red, 0);
          final int closest = DitherUtils.getBestFullColor(palette, red, green, blue);
          final int r = closest >> 16 & 0xFF;
          final int g = closest >> 8 & 0xFF;
          final int b = closest & 0xFF;
          final int delta_r = red - r;
          final int delta_g = green - g;
          final int delta_b = blue - b;
          if (hasNextX) {
            buf1[bufferIndex] = (delta_b * 32) / 200;
            buf1[bufferIndex - 1] = (delta_g * 32) / 200;
            buf1[bufferIndex - 2] = (delta_r * 32) / 200;
          }
          if (hasNextY) {
            if (x < widthMinus) {
              buf2[bufferIndex + 6] = (delta_b * 12) / 200;
              buf2[bufferIndex + 5] = (delta_g * 12) / 200;
              buf2[bufferIndex + 4] = (delta_r * 12) / 200;
            }
            if (x < width - 2) {
              buf2[bufferIndex + 9] = (delta_b * 5) / 200;
              buf2[bufferIndex + 8] = (delta_g * 5) / 200;
              buf2[bufferIndex + 7] = (delta_r * 5) / 200;
            }
            buf2[bufferIndex + 3] = (delta_b * 26) / 200;
            buf2[bufferIndex + 2] = (delta_g * 26) / 200;
            buf2[bufferIndex + 1] = (delta_r * 26) / 200;
            buf2[bufferIndex] = (delta_b * 30) / 200;
            buf2[bufferIndex - 1] = (delta_g * 30) / 200;
            buf2[bufferIndex - 2] = (delta_r * 30) / 200;
            if (x > 0) {
              buf2[bufferIndex - 3] = (delta_b * 16) / 200;
              buf2[bufferIndex - 4] = (delta_g * 16) / 200;
              buf2[bufferIndex - 5] = (delta_r * 16) / 200;
            }
            if (x > 1) {
              buf2[bufferIndex - 6] = (delta_b * 12) / 200;
              buf2[bufferIndex - 7] = (delta_g * 12) / 200;
              buf2[bufferIndex - 8] = (delta_r * 12) / 200;
            }
            if (y < height - 2) {
              if (x < widthMinus) {
                buf3[bufferIndex + 6] = (delta_b * 12) / 200;
                buf3[bufferIndex + 5] = (delta_g * 12) / 200;
                buf3[bufferIndex + 4] = (delta_r * 12) / 200;
              }
              if (x < width - 2) {
                buf3[bufferIndex + 9] = (delta_b * 5) / 200;
                buf3[bufferIndex + 8] = (delta_g * 5) / 200;
                buf3[bufferIndex + 7] = (delta_r * 5) / 200;
              }
              buf3[bufferIndex + 3] = (delta_b * 26) / 200;
              buf3[bufferIndex + 2] = (delta_g * 26) / 200;
              buf3[bufferIndex + 1] = (delta_r * 26) / 200;
              buf3[bufferIndex] = (delta_b * 30) / 200;
              buf3[bufferIndex - 1] = (delta_g * 30) / 200;
              buf3[bufferIndex - 2] = (delta_r * 30) / 200;
              if (x > 0) {
                buf3[bufferIndex - 3] = (delta_b * 16) / 200;
                buf3[bufferIndex - 4] = (delta_g * 16) / 200;
                buf3[bufferIndex - 5] = (delta_r * 16) / 200;
              }
              if (x > 1) {
                buf3[bufferIndex - 6] = (delta_b * 12) / 200;
                buf3[bufferIndex - 7] = (delta_g * 12) / 200;
                buf3[bufferIndex - 8] = (delta_r * 12) / 200;
              }
            }
          }
          data.setByte(index, DitherUtils.getBestColor(palette, r, g, b));
        }
      }
    }
    return data.array();
  }

  @Override
  public byte[] ditherIntoMinecraftNatively(final int[] buffer, final int width) {
    throw new UnsupportedOperationException();
  }
}
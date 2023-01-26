/*
 * MIT License
 *
 * Copyright (c) 2023 Brandon Li
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.github.pulsebeat02.ezmediacore.player.buffered;

import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class BufferConfiguration {

  public static final BufferConfiguration BUFFER_5;
  public static final BufferConfiguration BUFFER_10;
  public static final BufferConfiguration BUFFER_15;
  public static final BufferConfiguration BUFFER_20;

  static {
    BUFFER_5 = ofBuffer(5);
    BUFFER_10 = ofBuffer(10);
    BUFFER_15 = ofBuffer(15);
    BUFFER_20 = ofBuffer(20);
  }

  private final int buffer;

  BufferConfiguration(final int buffer) {
    this.buffer = buffer;
  }

  @Contract(value = "_ -> new", pure = true)
  public static @NotNull BufferConfiguration ofBuffer(final int buffer) {
    return new BufferConfiguration(buffer);
  }

  @Contract(value = "_ -> new", pure = true)
  public static @NotNull BufferConfiguration ofBuffer(@NotNull final Number number) {
    return ofBuffer((int) number);
  }

  public int getBuffer() {
    return this.buffer;
  }

  @Contract(pure = true)
  @Override
  public @NotNull String toString() {
    return "{buffer=%d}".formatted(this.buffer);
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof BufferConfiguration)) {
      return false;
    }
    return ((BufferConfiguration) obj).buffer == this.buffer;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.buffer);
  }
}

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
package io.github.pulsebeat02.ezmediacore.player.input.implementation;

import static com.google.common.base.Preconditions.checkNotNull;

import io.github.pulsebeat02.ezmediacore.player.input.Input;
import java.io.File;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Objects;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class PathInput implements Input {

  private static final Input EMPTY_PATH;

  static {
    EMPTY_PATH = ofPath("");
  }

  private final Path path;

  PathInput(@NotNull final String path) {
    checkNotNull(path, "URL specified cannot be null!");
    try {
      this.path = Path.of(path);
    } catch (final InvalidPathException e) {
      throw new IllegalArgumentException("Invalid path %s!".formatted(path));
    }
  }

  @Contract(value = "_ -> new", pure = true)
  public static @NotNull Input ofPath(@NotNull final String path) {
    return new PathInput(path);
  }

  @Contract(value = "_ -> new", pure = true)
  public static @NotNull Input ofPath(@NotNull final Path path) {
    return ofPath(path.toString());
  }

  @Contract(value = "_ -> new", pure = true)
  public static @NotNull Input ofPath(@NotNull final File file) {
    return ofPath(file.toString());
  }

  public static @NotNull Input emptyPath() {
    return EMPTY_PATH;
  }

  @Override
  public @NotNull String getInput() {
    return this.path.toString();
  }

  @Override
  public void setupInput() {}

  @Contract(pure = true)
  @Override
  public @NotNull String toString() {
    return "{path=%s}".formatted(this.path);
  }

  @Override
  public boolean equals(final Object obj) {
    if (!(obj instanceof PathInput)) {
      return false;
    }
    return ((PathInput) obj).path.equals(this.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.path);
  }
}

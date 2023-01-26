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
package io.github.pulsebeat02.ezmediacore.callback.audio;

import io.github.pulsebeat02.ezmediacore.MediaLibraryCore;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class ServerCallback extends AudioOutput implements ServerCallbackProxy {

  private final String host;
  private final int port;

  public ServerCallback(
      @NotNull final MediaLibraryCore core, @NotNull final String host, final int port) {
    super(core);
    this.host = host;
    this.port = port;
  }

  @Override
  public @NotNull String getHost() {
    return this.host;
  }

  @Override
  public int getPort() {
    return this.port;
  }

  public abstract static sealed class Builder extends AudioOutputBuilder
      permits FFmpegHttpServerCallback.Builder,
          PackCallback.Builder,
          VLCHttpServerCallback.Builder {

    private String host = "localhost";
    private int port;

    @Contract("_ -> this")
    public @NotNull Builder host(@NotNull final String host) {
      this.host = host;
      return this;
    }

    @Contract("_ -> this")
    public @NotNull Builder port(final int port) {
      this.port = port;
      return this;
    }

    protected int getPort() {
      return this.port;
    }

    protected String getHost() {
      return this.host;
    }
  }
}

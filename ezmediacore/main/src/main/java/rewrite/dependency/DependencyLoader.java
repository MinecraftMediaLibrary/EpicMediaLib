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
package rewrite.dependency;

import io.github.pulsebeat02.ezmediacore.EzMediaCore;
import rewrite.capabilities.Capabilities;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DependencyLoader {

  private final EzMediaCore core;
  private final ExecutorService service;

  DependencyLoader( final EzMediaCore core) {
    this.core = core;
    this.service = Executors.newFixedThreadPool(2);
  }

  public void start() {
    this.downloadNativeLibraries();
    this.shutdown();
  }

  private void downloadNativeLibraries() {
    final CompletableFuture<Void> vlc = CompletableFuture.runAsync(Capabilities.VLC::isEnabled, this.service);
    final CompletableFuture<Void> ffmpeg = CompletableFuture.runAsync(Capabilities.FFMPEG::isEnabled, this.service);
    final CompletableFuture<Void> rtsp = CompletableFuture.runAsync(Capabilities.RTSP::isEnabled, this.service);
    final CompletableFuture<Void> dither = CompletableFuture.runAsync(this::installNativeLibraries, this.service);
    CompletableFuture.allOf(vlc, ffmpeg, rtsp, dither).join();
  }

  private void shutdown() {
    this.service.shutdown();
  }

  private void installNativeLibraries() {
    final DitherDependencyManager dither = new DitherDependencyManager(this.core);
    dither.start();
  }

  public EzMediaCore getCore() {
    return this.core;
  }
}
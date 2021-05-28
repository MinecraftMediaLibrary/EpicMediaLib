/*............................................................................................
. Copyright © 2021 Brandon Li                                                               .
.                                                                                           .
. Permission is hereby granted, free of charge, to any person obtaining a copy of this      .
. software and associated documentation files (the “Software”), to deal in the Software     .
. without restriction, including without limitation the rights to use, copy, modify, merge, .
. publish, distribute, sublicense, and/or sell copies of the Software, and to permit        .
. persons to whom the Software is furnished to do so, subject to the following conditions:  .
.                                                                                           .
. The above copyright notice and this permission notice shall be included in all copies     .
. or substantial portions of the Software.                                                  .
.                                                                                           .
. THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,                           .
.  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF                       .
.   MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND                                   .
.   NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS                     .
.   BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN                      .
.   ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN                       .
.   CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE                        .
.   SOFTWARE.                                                                               .
............................................................................................*/

package io.github.pulsebeat02.minecraftmedialibrary.concurrent;

import io.github.pulsebeat02.minecraftmedialibrary.resourcepack.PackHolder;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

/** An async helper class used for building resourcepacks. */
public class AsyncResourcepackBuilder {

  private final PackHolder packHolder;

  /**
   * Instantiates a new AsyncResourcepackBuilder.
   *
   * @param packHolder the pack holder
   */
  public AsyncResourcepackBuilder(@NotNull final PackHolder packHolder) {
    this.packHolder = packHolder;
  }

  /**
   * Build a resource pack using CompletableFuture.
   *
   * @return the CompletableFuture
   */
  @NotNull
  public CompletableFuture<Void> buildResourcePack() {
    return CompletableFuture.runAsync(packHolder::buildResourcePack);
  }
}

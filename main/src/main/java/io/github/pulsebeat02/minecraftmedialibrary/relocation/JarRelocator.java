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

package io.github.pulsebeat02.minecraftmedialibrary.relocation;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

/** Relocates classes and resources within a jar file. */
public final class JarRelocator {

  /** The input jar */
  private final File input;
  /** The output jar */
  private final File output;
  /** The relocating remapper */
  private final RelocatingRemapper remapper;

  /** If the {@link #run()} method has been called yet */
  private final AtomicBoolean used = new AtomicBoolean(false);

  /**
   * Creates a new instance with the given settings.
   *
   * @param input the input jar file
   * @param output the output jar file
   * @param relocations the relocations
   */
  public JarRelocator(
      final File input, final File output, final Collection<Relocation> relocations) {
    this.input = input;
    this.output = output;
    remapper = new RelocatingRemapper(relocations);
  }

  /**
   * Creates a new instance with the given settings.
   *
   * @param input the input jar file
   * @param output the output jar file
   * @param relocations the relocations
   */
  public JarRelocator(final File input, final File output, final Map<String, String> relocations) {
    this.input = input;
    this.output = output;
    final Collection<Relocation> c = new ArrayList<>(relocations.size());
    for (final Map.Entry<String, String> entry : relocations.entrySet()) {
      c.add(new Relocation(entry.getKey(), entry.getValue()));
    }
    remapper = new RelocatingRemapper(c);
  }

  /**
   * Executes the relocation task
   *
   * @throws IOException if an exception is encountered whilst performing i/o with the input or
   *     output file
   */
  public void run() throws IOException {
    if (used.getAndSet(true)) {
      throw new IllegalStateException("#run has already been called on this instance");
    }

    try (final JarOutputStream out =
        new JarOutputStream(new BufferedOutputStream(new FileOutputStream(output)))) {
      try (final JarFile in = new JarFile(input)) {
        final JarRelocatorTask task = new JarRelocatorTask(remapper, out, in);
        task.processEntries();
      }
    }
  }
}

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

package io.github.pulsebeat02.minecraftmedialibrary.frame.entity;

import io.github.pulsebeat02.minecraftmedialibrary.MediaLibrary;
import io.github.pulsebeat02.minecraftmedialibrary.frame.FrameGrabberType;
import io.github.pulsebeat02.minecraftmedialibrary.frame.JavaCVVideoPlayer;
import io.github.pulsebeat02.minecraftmedialibrary.logger.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A VLCJ integrated player used to play videos in Minecraft. The library uses a callback for the
 * specific function from native libraries. It renders it on entities. Linux compatible.
 */
public class LinuxEntityPlayer extends JavaCVVideoPlayer {

  private final Location location;
  private final Entity[] entities;

  /**
   * Instantiates a new LinuxEntityPlayer.
   *
   * @param library the library
   * @param type the type
   * @param arg the arg
   * @param width the width
   * @param height the height
   * @param callback the callback
   * @param location the location
   */
  public LinuxEntityPlayer(
      @NotNull final MediaLibrary library,
      @NotNull final FrameGrabberType type,
      @NotNull final Object arg,
      @NotNull final EntityCallbackPrototype callback,
      @NotNull final Location location,
      final int width,
      final int height) {
    super(library, type, arg, width, height, callback);
    this.location = location;
    entities = callback.getEntities();
    Logger.info(String.format("Created a VLCJ Integrated Entity Cloud Video Player (%s)", arg));
  }

  /**
   * Returns a new builder class to use.
   *
   * @return the builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Starts player.
   *
   * @param players which players to play the audio for
   */
  @Override
  public void start(@NotNull final Collection<? extends Player> players) {
    removeEntities();
    super.start(players);
  }

  /** Releases the media player. */
  @Override
  public void release() {
    removeEntities();
    super.release();
  }

  /** Removes all entities. */
  public void removeEntities() {
    if (entities != null) {
      for (final Entity entity : entities) {
        entity.remove();
      }
    }
  }

  /**
   * Gets the location of the player.
   *
   * @return the location
   */
  public Location getLocation() {
    return location;
  }

  /**
   * Gets the entity array.
   *
   * @return the entity array
   */
  public Entity[] getEntities() {
    return entities;
  }

  /** The type Builder. */
  public static class Builder {

    private String url;
    private int width = 5;
    private int height = 5;
    private EntityCallbackPrototype callback;
    private Location location;
    private FrameGrabberType type;
    private Object arg;

    private Builder() {}

    public Builder setUrl(final String url) {
      this.url = url;
      return this;
    }

    public Builder setWidth(final int width) {
      this.width = width;
      return this;
    }

    public Builder setHeight(final int height) {
      this.height = height;
      return this;
    }

    public Builder setCallback(final EntityCallbackPrototype callback) {
      this.callback = callback;
      return this;
    }

    public Builder setLocation(final Location location) {
      this.location = location;
      return this;
    }

    public Builder setVideoType(final FrameGrabberType type) {
      this.type = type;
      return this;
    }

    public Builder setArgument(final Object arg) {
      this.arg = arg;
      return this;
    }

    public LinuxEntityPlayer build(@NotNull final MediaLibrary library) {
      return new LinuxEntityPlayer(library, type, arg, callback, location, width, height);
    }
  }
}
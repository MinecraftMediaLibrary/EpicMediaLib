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

package io.github.pulsebeat02.deluxemediaplugin.config;

import io.github.pulsebeat02.deluxemediaplugin.DeluxeMediaPlugin;
import io.github.pulsebeat02.minecraftmedialibrary.extractor.ExtractionConfiguration;
import io.github.pulsebeat02.minecraftmedialibrary.extractor.ExtractionSetting;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class EncoderConfiguration extends ConfigurationProvider {

  private ExtractionConfiguration settings;

  public EncoderConfiguration(@NotNull final DeluxeMediaPlugin plugin) {
    super(plugin, "configuration/encoder.yml");
  }

  @Override
  void deserialize() {
    final FileConfiguration configuration = getFileConfiguration();
    configuration.set("bitrate", settings.getBitrate());
    configuration.set("channels", settings.getChannels());
    configuration.set("sampling-rate", settings.getSamplingRate());
    configuration.set("volume", settings.getVolume());
    saveConfig();
  }

  @Override
  void serialize() {
    final FileConfiguration configuration = getFileConfiguration();
    final int bitrate = configuration.getInt("bitrate");
    final int channels = configuration.getInt("channels");
    final int samplingRate = configuration.getInt("sampling-rate");
    final int volume = configuration.getInt("volume");
    settings = new ExtractionSetting("libvorbis", bitrate, channels, samplingRate, volume);
  }

  public ExtractionConfiguration getSettings() {
    return settings;
  }
}

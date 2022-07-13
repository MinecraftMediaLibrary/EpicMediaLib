package io.github.pulsebeat02.deluxemediaplugin.command.video.load;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static io.github.pulsebeat02.deluxemediaplugin.command.Permission.has;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.pulsebeat02.deluxemediaplugin.DeluxeMediaPlugin;
import io.github.pulsebeat02.deluxemediaplugin.command.CommandSegment;
import io.github.pulsebeat02.deluxemediaplugin.command.video.ScreenConfig;
import io.github.pulsebeat02.deluxemediaplugin.locale.Locale;
import io.github.pulsebeat02.ezmediacore.player.input.implementation.UrlInput;
import java.io.IOException;
import java.net.URL;
import net.kyori.adventure.audience.Audience;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public final class LoadUrlCommand implements CommandSegment.Literal<CommandSender> {

  private final DeluxeMediaPlugin plugin;
  private final LiteralCommandNode<CommandSender> node;
  private final ScreenConfig config;
  private final LoadVideoCommand command;

  public LoadUrlCommand(
      @NotNull final DeluxeMediaPlugin plugin,
      @NotNull final ScreenConfig config,
      @NotNull final LoadVideoCommand command) {
    this.plugin = plugin;
    this.config = config;
    this.command = command;
    this.node =
        this.literal("url")
            .requires(has("deluxemediaplugin.command.video.load.url"))
            .then(
                this.argument("resource", StringArgumentType.greedyString())
                    .executes(this::handleUrl))
            .build();
  }

  private int handleUrl(final CommandContext<CommandSender> context) {

    final Audience audience = this.plugin.audience().sender(context.getSource());
    final String url = context.getArgument("resource", String.class);

    if (this.checkInvalidPlayer(audience)) {
      return SINGLE_SUCCESS;
    }

    if (this.checkInvalidUrl(audience, url)) {
      return SINGLE_SUCCESS;
    }

    this.setUrlMedia(url);

    audience.sendMessage(Locale.SET_MEDIA.build("url"));

    this.command.loadVideo(audience);

    return SINGLE_SUCCESS;
  }

  private void setUrlMedia(@NotNull final String url) {
    this.config.setMedia(UrlInput.ofUrl(url));
  }

  private boolean checkInvalidUrl(@NotNull final Audience audience, @NotNull final String url) {
    try {
      new URL(url).openConnection();
      return false;
    } catch (final IOException e) {
      audience.sendMessage(Locale.INVALID_URL.build());
      return true;
    }
  }

  private boolean checkInvalidPlayer(@NotNull final Audience audience) {
    return false;

    // For strict use. However, we can download the URL manually and pass it into JCodec.
    //        handleFalse(
    //        audience,
    //        Locale.ERR_INVALID_PLAYER_MEDIA.build("JCodec", "URLs"),
    //        this.config.getPlayer() instanceof JCodecMediaPlayer);
  }

  @Override
  public @NotNull LiteralCommandNode<CommandSender> getNode() {
    return this.node;
  }
}

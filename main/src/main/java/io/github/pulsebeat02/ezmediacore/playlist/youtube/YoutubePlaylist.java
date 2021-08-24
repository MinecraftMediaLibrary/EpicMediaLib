package io.github.pulsebeat02.ezmediacore.playlist.youtube;

import com.github.kiulian.downloader.downloader.request.RequestPlaylistInfo;
import com.github.kiulian.downloader.model.playlist.PlaylistDetails;
import com.github.kiulian.downloader.model.playlist.PlaylistInfo;
import io.github.pulsebeat02.ezmediacore.throwable.UnknownPlaylistException;
import io.github.pulsebeat02.ezmediacore.utility.MediaExtractionUtils;
import io.github.pulsebeat02.ezmediacore.utility.ResponseUtils;
import java.util.List;
import java.util.stream.Collectors;
import org.jcodec.codecs.mjpeg.tools.AssertionException;
import org.jetbrains.annotations.NotNull;

public class YoutubePlaylist implements Playlist {

  private final String url;
  private final String id;
  private final PlaylistInfo info;
  private final PlaylistDetails details;
  private final List<Video> videos;

  public YoutubePlaylist(@NotNull final String url) {
    this.url = url;
    this.id = MediaExtractionUtils.getYoutubeID(url)
        .orElseThrow(() -> new UnknownPlaylistException(url));
    this.info = this.getPlaylistResponse(0);
    this.details = this.info.details();
    this.videos =
        this.info.videos().parallelStream()
            .map(link -> new YoutubeVideo(link.videoId()))
            .collect(Collectors.toList());
  }

  private PlaylistInfo getPlaylistResponse(final int tries) {
    if (tries == 10) {
      throw new AssertionException("Request failure with video ID %s! Please try again.".formatted(
          this.id));
    }
    final int num = tries + 1;
    return ResponseUtils.getResponseResult(YoutubeProvider.getYoutubeDownloader()
            .getPlaylistInfo(new RequestPlaylistInfo(this.id)))
        .orElseGet(() -> this.getPlaylistResponse(
            num));
  }

  @Override
  public @NotNull String getAuthor() {
    return this.details.author();
  }

  @Override
  public int getVideoCount() {
    return this.details.videoCount();
  }

  @Override
  public @NotNull String getUrl() {
    return this.url;
  }

  @Override
  public @NotNull String getName() {
    return this.details.title();
  }

  @Override
  public @NotNull List<Video> getVideos() {
    return this.videos;
  }

  @Override
  public long getViewCount() {
    return this.details.viewCount();
  }

  @NotNull PlaylistInfo getPlaylistInfo() {
    return this.info;
  }

  @NotNull PlaylistDetails getPlaylistDetails() {
    return this.details;
  }
}
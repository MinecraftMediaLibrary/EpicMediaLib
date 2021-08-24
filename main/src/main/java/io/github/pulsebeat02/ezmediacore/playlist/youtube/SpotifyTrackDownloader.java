package io.github.pulsebeat02.ezmediacore.playlist.youtube;

import com.github.kiulian.downloader.downloader.request.RequestVideoFileDownload;
import com.wrapper.spotify.exceptions.SpotifyWebApiException;
import io.github.pulsebeat02.ezmediacore.MediaLibraryCore;
import io.github.pulsebeat02.ezmediacore.playlist.spotify.QuerySearch;
import io.github.pulsebeat02.ezmediacore.playlist.spotify.SpotifyTrack;
import io.github.pulsebeat02.ezmediacore.playlist.spotify.Track;
import io.github.pulsebeat02.ezmediacore.playlist.spotify.TrackDownloader;
import io.github.pulsebeat02.ezmediacore.utility.PathUtils;
import io.github.pulsebeat02.ezmediacore.utility.ResponseUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import org.apache.commons.io.FilenameUtils;
import org.apache.hc.core5.http.ParseException;
import org.jetbrains.annotations.NotNull;

public class SpotifyTrackDownloader implements TrackDownloader {

  private final SpotifyQuerySearch searcher;
  private final Track track;
  private final Path videoPath;

  public SpotifyTrackDownloader(@NotNull final Track track, @NotNull final Path videoPath)
      throws IOException {
    this.searcher = new SpotifyQuerySearch(track);
    this.track = track;
    this.videoPath = videoPath;
  }

  public SpotifyTrackDownloader(@NotNull final String url, @NotNull final Path videoPath)
      throws IOException, ParseException, SpotifyWebApiException {
    this(new SpotifyTrack(url), videoPath);
  }

  public SpotifyTrackDownloader(@NotNull final MediaLibraryCore core, @NotNull final String url)
      throws IOException, ParseException, SpotifyWebApiException {
    this(new SpotifyTrack(url), core.getVideoPath().resolve("%s.mp4".formatted(UUID.randomUUID())));
  }

  @Override
  public void downloadVideo(@NotNull final VideoQuality format, final boolean overwrite) {
    this.onStartVideoDownload();
    this.internalDownload(new RequestVideoFileDownload(this.getFormat(format))
        .saveTo(this.videoPath.getParent().toFile())
        .renameTo(FilenameUtils.removeExtension(PathUtils.getName(this.videoPath)))
        .overwriteIfExists(overwrite));
    this.onFinishVideoDownload();
  }

  private void internalDownload(@NotNull final RequestVideoFileDownload download) {
    if (ResponseUtils.getResponseResult(
        YoutubeProvider.getYoutubeDownloader().downloadVideoFile(download)).isEmpty()) {
      this.internalDownload(download);
    }
  }

  private com.github.kiulian.downloader.model.videos.formats.VideoFormat getFormat(
      @NotNull final VideoQuality format) {
    return this.searcher.getRawVideo().getVideoInfo().videoFormats().stream()
        .filter(
            f ->
                f.videoQuality()
                    .equals(YoutubeVideoFormat.getVideoFormatMappings().inverse().get(format)))
        .findAny()
        .orElseThrow(IllegalArgumentException::new);
  }

  @Override
  public void onStartVideoDownload() {
  }

  @Override
  public void onFinishVideoDownload() {
  }


  @Override
  public @NotNull Path getDownloadPath() {
    return this.videoPath;
  }

  @Override
  public @NotNull Track getTrack() {
    return this.track;
  }

  @Override
  public @NotNull QuerySearch getSearcher() {
    return this.searcher;
  }
}
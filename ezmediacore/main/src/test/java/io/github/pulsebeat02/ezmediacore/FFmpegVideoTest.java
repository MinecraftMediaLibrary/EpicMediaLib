package io.github.pulsebeat02.ezmediacore;

import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.Frame;
import com.github.kokorin.jaffree.ffmpeg.FrameConsumer;
import com.github.kokorin.jaffree.ffmpeg.FrameOutput;
import com.github.kokorin.jaffree.ffmpeg.Stream;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.time.Instant;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.jetbrains.annotations.Contract;


public class FFmpegVideoTest {

  private final ArrayBlockingQueue<Entry<BufferedImage, Long>> frames;
  private final JFrame window;
  private final AtomicBoolean running;
  private long startEpoch;
  private FFmpeg ffmpeg;

  FFmpegVideoTest() {
    this.frames = new ArrayBlockingQueue<>(30);
    this.running = new AtomicBoolean(true);
    final Path bin = Path.of("/Users/bli24/Desktop/ffmpeg/ffmpeg-x86_64-osx");
    final Path in = Path.of("/Users/bli24/Downloads/test.mp4");
    this.window = new JFrame("Example Video");
    this.window.setSize(1024, 2048);
    this.window.setVisible(true);
    this.window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    this.window.addWindowListener(
        new WindowAdapter() {
          @Override
          public void windowClosing( final WindowEvent windowEvent) {
            FFmpegVideoTest.this.running.set(false);
          }
        });
    this.init(bin, in);
    this.ffmpeg.execute();
  }

  public static void main(final String[] args) {
    new FFmpegVideoTest();
  }

  private void init( final Path path,  final Path input) {
    this.ffmpeg =
        new FFmpeg(path)
            .addInput(UrlInput.fromPath(input).setPosition(0).addArgument("-re"))
            .addOutput(FrameOutput.withConsumer(this.getFrameConsumer()))
            .setLogLevel(LogLevel.FATAL)
            .setProgressListener(line -> {})
            .setOutputListener(line -> {});
    this.startEpoch = Instant.now().toEpochMilli();
    this.displayThread();
  }

  private  FrameConsumer getFrameConsumer() {
    return new FrameConsumer() {

      private float[] calculations;

      @Override
      public void consumeStreams( final List<Stream> streams) {

        // if stream ids are not properly ordered, sometimes we need to rearrange them
        final int max = streams.stream().mapToInt(Stream::getId).max().orElseThrow();

        // create our lookup table
        this.calculations = new float[max + 1];

        // loop through elements, set id with proper stream timebase
        for (final Stream stream : streams) {
          this.calculations[stream.getId()] = (1.0F / stream.getTimebase()) * 1000;
        }
      }

      @Override
      public void consume(final Frame frame) {

        // sometimes ffmpeg returns a null frame...
        if (frame == null) {
          return;
        }

        // sometimes it is an audio frame (or non-video frame in general). We don't want audio
        // frames
        final BufferedImage image = frame.getImage();
        if (image == null) {
          return;
        }

        // hack to wait for new frames
        while (FFmpegVideoTest.this.frames.remainingCapacity() <= 1) {
          Try.sleep(TimeUnit.MILLISECONDS, 5);
        }

        final long stamp = this.calculateTimeStamp(frame);

        // add to queue
        FFmpegVideoTest.this.frames.add(new SimpleImmutableEntry<>(image, stamp));
      }

      private long calculateTimeStamp( final Frame frame) {
        return (long) (frame.getPts() * this.calculations[frame.getStreamId()]);
      }
    };
  }

  public void displayThread() {
    CompletableFuture.allOf(
        CompletableFuture.runAsync(this.getDisplayRunnable()),
        CompletableFuture.runAsync(this.getSkipRunnable()));
  }

  private  Runnable getDisplayRunnable() {
    return () -> {
      while (this.running.get()) {
        try {
          // process current frame
          this.displayFrame(this.frames.take().getKey());
        } catch (final InterruptedException ignored) {
        }
      }
    };
  }

  private  Runnable getSkipRunnable() {
    return () -> {
      while (this.running.get()) {
        // skip frames if necessary
        // For example, if the passed time is 40 ms, and the time of the
        // current frame is 30 ms, we have to skip. If it is equal, we are
        // bound to get behind, so skip
        try {
          final long passed = Instant.now().toEpochMilli() - this.startEpoch;
          Entry<BufferedImage, Long> skip = this.frames.take();
          while (skip.getValue() <= passed) {
            this.frames.take();
            skip = this.frames.take();
          }
        } catch (final InterruptedException ignored) {
        }
      }
    };
  }

  private void displayFrame( final BufferedImage image) {
    this.window.getContentPane().removeAll();
    this.window.add(new JLabel("", new ImageIcon(image), JLabel.CENTER));
    this.window.repaint();
    this.window.revalidate();
  }
}
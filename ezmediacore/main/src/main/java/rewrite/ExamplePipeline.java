package rewrite;

import rewrite.pipeline.FramePipeline;
import rewrite.pipeline.BasicPipelineResult;
import rewrite.pipeline.frame.FramePacket;
import rewrite.pipeline.grabbers.FFmpegGrabberPlayer;
import rewrite.pipeline.input.Input;
import rewrite.pipeline.input.parser.URLInputParser;
import rewrite.pipeline.input.parser.strategy.DefaultAudioStrategy;
import rewrite.pipeline.input.parser.strategy.DefaultVideoStrategy;
import rewrite.pipeline.outputs.video.MapFrameOutput;
import rewrite.pipeline.steps.FloydSteinbergDitherPipelineStep;

import java.util.concurrent.CompletableFuture;

public final class ExamplePipeline {

  public void createPipeline() {

    final BasicPipelineResult result = FramePipeline.source(FramePacket.class)
            .thenPipe(new FloydSteinbergDitherPipelineStep())
            .thenFinally(new MapFrameOutput(640, 480, 4, 4, 0));

    final FFmpegGrabberPlayer player = new FFmpegGrabberPlayer(result);
    final URLInputParser parser = new URLInputParser("https://www.youtube.com/watch?v=dQw4w9WgXcQ");

    final CompletableFuture<Input> video = parser.retrieveVideoInput(new DefaultVideoStrategy());
    final CompletableFuture<Input> audio = parser.retrieveAudioInput(new DefaultAudioStrategy());
    final String[] arguments = new String[] {};
    player.play(video.join(), audio.join(), arguments);
  }
}
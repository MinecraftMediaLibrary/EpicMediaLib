package rewrite.pipeline.steps;

import rewrite.dither.algorithm.ordered.OrderedDither;
import rewrite.dither.algorithm.ordered.OrderedPixelMapper;
import rewrite.pipeline.frame.FramePacket;

public final class OrderedDitherPipelineStep implements FramePipelineStep<FramePacket, FramePacket> {

  private final OrderedDither dither;

  public OrderedDitherPipelineStep(final OrderedPixelMapper mapper) {
    this.dither = new OrderedDither(mapper);
  }

  @Override
  public FramePacket process(final FramePacket input) {
    final int[] rgb = input.getRGBSamples();
    final int width = input.getImageWidth();
    this.dither.dither(rgb, width);
    return input;
  }
}

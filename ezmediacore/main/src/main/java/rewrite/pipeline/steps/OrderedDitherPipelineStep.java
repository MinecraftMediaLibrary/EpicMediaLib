package rewrite.pipeline.steps;

import rewrite.dither.algorithm.ordered.OrderedDither;
import rewrite.dither.algorithm.ordered.OrderedPixelMapper;
import rewrite.dither.load.ColorPalette;
import rewrite.pipeline.frame.DitheredFramePacket;
import rewrite.pipeline.frame.FramePacket;

public final class OrderedDitherPipelineStep implements FramePipelineStep<FramePacket, DitheredFramePacket> {

  private final OrderedDither dither;

  public OrderedDitherPipelineStep(final ColorPalette palette, final OrderedPixelMapper mapper) {
    this.dither = new OrderedDither(palette, mapper);
  }

  @Override
  public DitheredFramePacket process(final FramePacket input) {
    final int[] rgb = input.getRGBSamples();
    final int width = input.getImageWidth();
    final byte[] dithered = this.dither.ditherIntoMinecraft(rgb, width);
    return DitheredFramePacket.create(input, dithered);
  }
}

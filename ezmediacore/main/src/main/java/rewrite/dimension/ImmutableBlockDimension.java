package rewrite.dimension;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class ImmutableBlockDimension implements BlockDimension {

  private final int width;
  private final int height;

  public ImmutableBlockDimension(final int width, final int height) {
    this.width = width;
    this.height = height;
  }

  @Override
  public @NotNull Map<String, Object> serialize() {
    return Map.of(
            "width", this.width,
            "height", this.height);
  }

  @Override
  public int getWidth() {
    return this.width;
  }

  @Override
  public int getHeight() {
    return this.height;
  }
}
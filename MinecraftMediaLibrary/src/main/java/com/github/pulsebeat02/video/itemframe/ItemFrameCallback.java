package com.github.pulsebeat02.video.itemframe;

import com.github.pulsebeat02.video.dither.AbstractDitherHolder;
import com.github.pulsebeat02.video.dither.FloydImageDither;
import com.github.pulsebeat02.MinecraftMediaLibrary;
import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.UUID;

public class ItemFrameCallback implements AbstractCallback {

    private final MinecraftMediaLibrary library;
    private final UUID[] viewers;
    private final AbstractDitherHolder type;
    private final int map;
    private int width;
    private int height;
    private final int videoWidth;
    private final int delay;
    private long lastUpdated;

    public ItemFrameCallback(@NotNull final MinecraftMediaLibrary library,
                             @NotNull final UUID[] viewers,
                             final int map,
                             final int width, final int height, final int videoWidth,
                             final int delay,
                             final AbstractDitherHolder type) {
        this.library = library;
        this.viewers = viewers;
        this.type = type;
        this.map = map;
        this.width = width;
        this.height = height;
        this.videoWidth = videoWidth;
        this.delay = delay;
    }

    public UUID[] getViewers() {
        return viewers;
    }

    public int getMap() {
        return map;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDelay() {
        return delay;
    }

    public class ItemFrameCallbackBuilder {

        private UUID[] viewers;
        private AbstractDitherHolder type;
        private int map;
        private int width;
        private int height;
        private int videoWidth;
        private int delay;

        public ItemFrameCallbackBuilder setViewers(final UUID[] viewers) {
            this.viewers = viewers;
            return this;
        }

        public ItemFrameCallbackBuilder setMap(final int map) {
            this.map = map;
            return this;
        }

        public ItemFrameCallbackBuilder setWidth(final int width) {
            this.width = width;
            return this;
        }

        public ItemFrameCallbackBuilder setHeight(final int height) {
            this.height = height;
            return this;
        }

        public ItemFrameCallbackBuilder setVideoWidth(final int videoWidth) {
            this.videoWidth = videoWidth;
            return this;
        }

        public ItemFrameCallbackBuilder setDelay(final int delay) {
            this.delay = delay;
            return this;
        }

        public ItemFrameCallbackBuilder setDitherHolder(final AbstractDitherHolder holder) {
            this.type = holder;
            return this;
        }

        public ItemFrameCallback createItemFrameCallback(final MinecraftMediaLibrary library) {
            return new ItemFrameCallback(library, viewers, map, width, height, videoWidth, delay, type);
        }

    }

    public void send(final int[] data) {
        long time = System.currentTimeMillis();
        long difference = time - lastUpdated;
        if (difference >= delay) {
            lastUpdated = time;
            ByteBuffer dithered = type.ditherIntoMinecraft(data, videoWidth);
            library.getHandler().display(viewers, map, width, height, dithered, videoWidth);
        }
    }

    public MinecraftMediaLibrary getLibrary() {
        return library;
    }

    public void setWidth(final int width) { this.width = width; }

    public void setHeight(final int height) { this.height = height; }

    public int getVideoWidth() {
        return videoWidth;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public AbstractDitherHolder getType() {
        return type;
    }
}
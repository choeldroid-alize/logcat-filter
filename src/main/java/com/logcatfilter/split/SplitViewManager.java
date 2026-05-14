package com.logcatfilter.split;

import com.logcatfilter.buffer.LogBuffer;

import java.util.Optional;

/**
 * Manages the split-view state, coordinating two independent LogBuffer panes
 * and handling resize and sync-scroll events.
 */
public class SplitViewManager {

    private SplitViewConfig config;
    private LogBuffer primaryBuffer;
    private LogBuffer secondaryBuffer;
    private int primaryPaneSize;    // lines available in primary pane
    private int secondaryPaneSize;  // lines available in secondary pane

    public SplitViewManager(SplitViewConfig config, LogBuffer primaryBuffer) {
        if (config == null) throw new IllegalArgumentException("config must not be null");
        if (primaryBuffer == null) throw new IllegalArgumentException("primaryBuffer must not be null");
        this.config = config;
        this.primaryBuffer = primaryBuffer;
        this.secondaryBuffer = null;
    }

    public void attachSecondaryBuffer(LogBuffer buffer) {
        this.secondaryBuffer = buffer;
    }

    public void detachSecondaryBuffer() {
        this.secondaryBuffer = null;
    }

    public Optional<LogBuffer> getSecondaryBuffer() {
        return Optional.ofNullable(secondaryBuffer);
    }

    public LogBuffer getPrimaryBuffer() {
        return primaryBuffer;
    }

    /**
     * Recalculates pane sizes based on total available lines and current split ratio.
     *
     * @param totalLines total terminal lines available for log content
     */
    public void resize(int totalLines) {
        if (totalLines <= 0) throw new IllegalArgumentException("totalLines must be positive");
        if (!config.isEnabled() || secondaryBuffer == null) {
            primaryPaneSize = totalLines;
            secondaryPaneSize = 0;
            return;
        }
        int divider = config.isShowDivider() ? 1 : 0;
        int usable = totalLines - divider;
        primaryPaneSize = (int) Math.floor(usable * config.getSplitRatio());
        secondaryPaneSize = usable - primaryPaneSize;
    }

    public int getPrimaryPaneSize() { return primaryPaneSize; }
    public int getSecondaryPaneSize() { return secondaryPaneSize; }

    public SplitViewConfig getConfig() { return config; }

    public void updateConfig(SplitViewConfig newConfig) {
        if (newConfig == null) throw new IllegalArgumentException("config must not be null");
        this.config = newConfig;
    }

    public boolean isSplitActive() {
        return config.isEnabled() && secondaryBuffer != null;
    }
}

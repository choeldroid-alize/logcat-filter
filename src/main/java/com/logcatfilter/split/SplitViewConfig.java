package com.logcatfilter.split;

/**
 * Configuration for split-pane view mode, allowing two log views side by side.
 */
public class SplitViewConfig {

    public enum Orientation { HORIZONTAL, VERTICAL }

    private boolean enabled;
    private Orientation orientation;
    private double splitRatio;  // 0.0 < ratio < 1.0, fraction for primary pane
    private boolean syncScroll;
    private boolean showDivider;

    public SplitViewConfig() {
        this.enabled = false;
        this.orientation = Orientation.HORIZONTAL;
        this.splitRatio = 0.5;
        this.syncScroll = false;
        this.showDivider = true;
    }

    public SplitViewConfig(boolean enabled, Orientation orientation, double splitRatio,
                           boolean syncScroll, boolean showDivider) {
        if (splitRatio <= 0.0 || splitRatio >= 1.0) {
            throw new IllegalArgumentException("splitRatio must be between 0 and 1 exclusive");
        }
        this.enabled = enabled;
        this.orientation = orientation != null ? orientation : Orientation.HORIZONTAL;
        this.splitRatio = splitRatio;
        this.syncScroll = syncScroll;
        this.showDivider = showDivider;
    }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Orientation getOrientation() { return orientation; }
    public void setOrientation(Orientation orientation) {
        this.orientation = orientation != null ? orientation : Orientation.HORIZONTAL;
    }

    public double getSplitRatio() { return splitRatio; }
    public void setSplitRatio(double splitRatio) {
        if (splitRatio <= 0.0 || splitRatio >= 1.0) {
            throw new IllegalArgumentException("splitRatio must be between 0 and 1 exclusive");
        }
        this.splitRatio = splitRatio;
    }

    public boolean isSyncScroll() { return syncScroll; }
    public void setSyncScroll(boolean syncScroll) { this.syncScroll = syncScroll; }

    public boolean isShowDivider() { return showDivider; }
    public void setShowDivider(boolean showDivider) { this.showDivider = showDivider; }

    @Override
    public String toString() {
        return "SplitViewConfig{enabled=" + enabled + ", orientation=" + orientation +
               ", splitRatio=" + splitRatio + ", syncScroll=" + syncScroll +
               ", showDivider=" + showDivider + "}";
    }
}

package com.logcatfilter.scroll;

/**
 * Tracks the current scroll position and follow-tail mode for the log viewer.
 */
public class ScrollState {

    private int totalLines;
    private int visibleLines;
    private int scrollOffset;
    private boolean followTail;

    public ScrollState(int visibleLines) {
        if (visibleLines <= 0) {
            throw new IllegalArgumentException("visibleLines must be positive");
        }
        this.visibleLines = visibleLines;
        this.totalLines = 0;
        this.scrollOffset = 0;
        this.followTail = true;
    }

    public void setTotalLines(int totalLines) {
        if (totalLines < 0) {
            throw new IllegalArgumentException("totalLines cannot be negative");
        }
        this.totalLines = totalLines;
        if (followTail) {
            scrollOffset = Math.max(0, totalLines - visibleLines);
        }
    }

    public void scrollUp(int lines) {
        followTail = false;
        scrollOffset = Math.max(0, scrollOffset - lines);
    }

    public void scrollDown(int lines) {
        int maxOffset = Math.max(0, totalLines - visibleLines);
        scrollOffset = Math.min(maxOffset, scrollOffset + lines);
        if (scrollOffset >= maxOffset) {
            followTail = true;
        }
    }

    public void scrollToTop() {
        followTail = false;
        scrollOffset = 0;
    }

    public void scrollToBottom() {
        followTail = true;
        scrollOffset = Math.max(0, totalLines - visibleLines);
    }

    public void scrollToLine(int lineIndex) {
        if (lineIndex < 0 || lineIndex >= totalLines) {
            throw new IndexOutOfBoundsException("lineIndex out of range: " + lineIndex);
        }
        followTail = false;
        int maxOffset = Math.max(0, totalLines - visibleLines);
        scrollOffset = Math.min(maxOffset, lineIndex);
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public boolean isFollowTail() {
        return followTail;
    }

    public int getTotalLines() {
        return totalLines;
    }

    public int getVisibleLines() {
        return visibleLines;
    }

    public void setVisibleLines(int visibleLines) {
        if (visibleLines <= 0) {
            throw new IllegalArgumentException("visibleLines must be positive");
        }
        this.visibleLines = visibleLines;
        if (followTail) {
            scrollOffset = Math.max(0, totalLines - visibleLines);
        }
    }
}

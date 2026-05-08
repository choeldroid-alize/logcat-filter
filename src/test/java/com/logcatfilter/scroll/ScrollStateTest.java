package com.logcatfilter.scroll;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScrollStateTest {

    private ScrollState state;

    @BeforeEach
    void setUp() {
        state = new ScrollState(20);
    }

    @Test
    void initialStateFollowsTail() {
        assertTrue(state.isFollowTail());
        assertEquals(0, state.getScrollOffset());
    }

    @Test
    void setTotalLinesUpdateOffsetWhenFollowTail() {
        state.setTotalLines(100);
        assertTrue(state.isFollowTail());
        assertEquals(80, state.getScrollOffset());
    }

    @Test
    void scrollUpDisablesFollowTail() {
        state.setTotalLines(100);
        state.scrollUp(10);
        assertFalse(state.isFollowTail());
        assertEquals(70, state.getScrollOffset());
    }

    @Test
    void scrollUpDoesNotGoBelowZero() {
        state.setTotalLines(100);
        state.scrollUp(200);
        assertEquals(0, state.getScrollOffset());
    }

    @Test
    void scrollDownReEnablesFollowTailAtBottom() {
        state.setTotalLines(100);
        state.scrollUp(30);
        assertFalse(state.isFollowTail());
        state.scrollDown(30);
        assertTrue(state.isFollowTail());
        assertEquals(80, state.getScrollOffset());
    }

    @Test
    void scrollToTopSetsOffsetZeroAndDisablesFollow() {
        state.setTotalLines(100);
        state.scrollToTop();
        assertFalse(state.isFollowTail());
        assertEquals(0, state.getScrollOffset());
    }

    @Test
    void scrollToBottomEnablesFollowTail() {
        state.setTotalLines(100);
        state.scrollToTop();
        state.scrollToBottom();
        assertTrue(state.isFollowTail());
        assertEquals(80, state.getScrollOffset());
    }

    @Test
    void scrollToLinePositionsCorrectly() {
        state.setTotalLines(100);
        state.scrollToLine(10);
        assertFalse(state.isFollowTail());
        assertEquals(10, state.getScrollOffset());
    }

    @Test
    void scrollToLineThrowsForOutOfBounds() {
        state.setTotalLines(50);
        assertThrows(IndexOutOfBoundsException.class, () -> state.scrollToLine(50));
        assertThrows(IndexOutOfBoundsException.class, () -> state.scrollToLine(-1));
    }

    @Test
    void setVisibleLinesUpdatesOffsetWhenFollowTail() {
        state.setTotalLines(100);
        state.setVisibleLines(10);
        assertEquals(90, state.getScrollOffset());
    }

    @Test
    void invalidConstructorArgThrows() {
        assertThrows(IllegalArgumentException.class, () -> new ScrollState(0));
        assertThrows(IllegalArgumentException.class, () -> new ScrollState(-5));
    }

    @Test
    void negativeTotalLinesThrows() {
        assertThrows(IllegalArgumentException.class, () -> state.setTotalLines(-1));
    }
}

package com.logcatfilter.pipeline;

import com.logcatfilter.dedup.DeduplicationEngine;
import com.logcatfilter.filter.FilterChain;
import com.logcatfilter.highlight.HighlightEngine;
import com.logcatfilter.parser.LogcatEntry;
import com.logcatfilter.stats.StatsCollector;
import com.logcatfilter.throttle.ThrottleEngine;
import com.logcatfilter.throttle.ThrottleResult;
import com.logcatfilter.watch.WatchManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProcessingPipelineTest {

    private FilterChain filterChain;
    private DeduplicationEngine dedupEngine;
    private ThrottleEngine throttleEngine;
    private HighlightEngine highlightEngine;
    private StatsCollector statsCollector;
    private WatchManager watchManager;
    private ProcessingPipeline pipeline;
    private LogcatEntry entry;

    @BeforeEach
    void setUp() {
        filterChain     = mock(FilterChain.class);
        dedupEngine     = mock(DeduplicationEngine.class);
        throttleEngine  = mock(ThrottleEngine.class);
        highlightEngine = mock(HighlightEngine.class);
        statsCollector  = mock(StatsCollector.class);
        watchManager    = mock(WatchManager.class);

        pipeline = new ProcessingPipeline(filterChain, dedupEngine, throttleEngine,
                                          highlightEngine, statsCollector, watchManager);

        entry = mock(LogcatEntry.class);
        when(entry.getTag()).thenReturn("MyTag");
        when(entry.getLevel()).thenReturn('D');
    }

    @Test
    void nullEntryReturnsEmpty() {
        assertTrue(pipeline.process(null).isEmpty());
    }

    @Test
    void filteredEntryReturnsEmpty() {
        when(filterChain.accepts(entry)).thenReturn(false);
        assertTrue(pipeline.process(entry).isEmpty());
        verifyNoInteractions(dedupEngine, throttleEngine, highlightEngine, statsCollector);
    }

    @Test
    void duplicateEntryReturnsEmpty() {
        when(filterChain.accepts(entry)).thenReturn(true);
        when(dedupEngine.isDuplicate(entry)).thenReturn(true);
        assertTrue(pipeline.process(entry).isEmpty());
        verifyNoInteractions(throttleEngine, highlightEngine, statsCollector);
    }

    @Test
    void throttleDroppedEntryReturnsEmpty() {
        when(filterChain.accepts(entry)).thenReturn(true);
        when(dedupEngine.isDuplicate(entry)).thenReturn(false);
        when(throttleEngine.evaluate(entry)).thenReturn(ThrottleResult.dropped());
        assertTrue(pipeline.process(entry).isEmpty());
        verifyNoInteractions(highlightEngine, statsCollector);
    }

    @Test
    void acceptedEntryProducesResult() {
        when(filterChain.accepts(entry)).thenReturn(true);
        when(dedupEngine.isDuplicate(entry)).thenReturn(false);
        when(throttleEngine.evaluate(entry)).thenReturn(ThrottleResult.allowed());
        when(highlightEngine.apply(entry)).thenReturn("\u001B[32mhello\u001B[0m");

        Optional<PipelineResult> result = pipeline.process(entry);

        assertTrue(result.isPresent());
        assertEquals(entry, result.get().getEntry());
        assertEquals("\u001B[32mhello\u001B[0m", result.get().getHighlightedText());
        assertFalse(result.get().isThrottleSuppressed());
        verify(statsCollector).record(entry);
        verify(watchManager).evaluate(entry);
    }

    @Test
    void listenerIsNotifiedOnAcceptedEntry() {
        when(filterChain.accepts(entry)).thenReturn(true);
        when(dedupEngine.isDuplicate(entry)).thenReturn(false);
        when(throttleEngine.evaluate(entry)).thenReturn(ThrottleResult.allowed());
        when(highlightEngine.apply(entry)).thenReturn("text");

        PipelineListener listener = mock(PipelineListener.class);
        pipeline.addListener(listener);
        pipeline.process(entry);

        verify(listener).onProcessed(any(PipelineResult.class));
    }

    @Test
    void removedListenerIsNotNotified() {
        when(filterChain.accepts(entry)).thenReturn(true);
        when(dedupEngine.isDuplicate(entry)).thenReturn(false);
        when(throttleEngine.evaluate(entry)).thenReturn(ThrottleResult.allowed());
        when(highlightEngine.apply(entry)).thenReturn("text");

        PipelineListener listener = mock(PipelineListener.class);
        pipeline.addListener(listener);
        pipeline.removeListener(listener);
        pipeline.process(entry);

        verifyNoInteractions(listener);
    }
}

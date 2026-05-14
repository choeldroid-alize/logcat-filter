package com.logcatfilter.pipeline;

import com.logcatfilter.parser.LogcatEntry;
import com.logcatfilter.filter.FilterChain;
import com.logcatfilter.highlight.HighlightEngine;
import com.logcatfilter.dedup.DeduplicationEngine;
import com.logcatfilter.throttle.ThrottleEngine;
import com.logcatfilter.stats.StatsCollector;
import com.logcatfilter.watch.WatchManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Orchestrates the full processing pipeline for each incoming LogcatEntry.
 * Stages: filter → dedup → throttle → highlight → stats/watch side-effects.
 */
public class ProcessingPipeline {

    private final FilterChain filterChain;
    private final DeduplicationEngine dedupEngine;
    private final ThrottleEngine throttleEngine;
    private final HighlightEngine highlightEngine;
    private final StatsCollector statsCollector;
    private final WatchManager watchManager;
    private final List<PipelineListener> listeners = new ArrayList<>();

    public ProcessingPipeline(FilterChain filterChain,
                               DeduplicationEngine dedupEngine,
                               ThrottleEngine throttleEngine,
                               HighlightEngine highlightEngine,
                               StatsCollector statsCollector,
                               WatchManager watchManager) {
        this.filterChain = filterChain;
        this.dedupEngine = dedupEngine;
        this.throttleEngine = throttleEngine;
        this.highlightEngine = highlightEngine;
        this.statsCollector = statsCollector;
        this.watchManager = watchManager;
    }

    /**
     * Processes a single entry through all pipeline stages.
     *
     * @param entry the raw parsed log entry
     * @return Optional containing the processed result, or empty if the entry was dropped
     */
    public Optional<PipelineResult> process(LogcatEntry entry) {
        if (entry == null) {
            return Optional.empty();
        }

        // Stage 1: Filter
        if (!filterChain.accepts(entry)) {
            return Optional.empty();
        }

        // Stage 2: Deduplication
        if (dedupEngine.isDuplicate(entry)) {
            return Optional.empty();
        }

        // Stage 3: Throttle
        var throttleResult = throttleEngine.evaluate(entry);
        if (throttleResult.isDropped()) {
            return Optional.empty();
        }

        // Stage 4: Highlight (produces annotated text)
        String highlighted = highlightEngine.apply(entry);

        // Stage 5: Side-effects (stats + watch alerts)
        statsCollector.record(entry);
        watchManager.evaluate(entry);

        PipelineResult result = new PipelineResult(entry, highlighted, throttleResult.isSuppressed());
        listeners.forEach(l -> l.onProcessed(result));
        return Optional.of(result);
    }

    public void addListener(PipelineListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    public void removeListener(PipelineListener listener) {
        listeners.remove(listener);
    }
}

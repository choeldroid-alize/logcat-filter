package com.logcatfilter.pid;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Parses and holds configuration for PID/TID based filtering.
 * Supports comma-separated PID lists and optional mode prefix.
 *
 * Format examples:
 *   "1234,5678"              -> allowlist by PID
 *   "block:1234,5678"        -> blocklist by PID
 *   "tid:1234"               -> allowlist by TID
 *   "block:tid:1234,5678"    -> blocklist by TID
 */
public class PidFilterConfig {

    private final List<Integer> pids;
    private final List<Integer> tids;
    private final PidFilter.Mode mode;

    private PidFilterConfig(List<Integer> pids, List<Integer> tids, PidFilter.Mode mode) {
        this.pids = pids;
        this.tids = tids;
        this.mode = mode;
    }

    public static PidFilterConfig parse(String spec) {
        if (spec == null || spec.isBlank()) {
            return new PidFilterConfig(List.of(), List.of(), PidFilter.Mode.ALLOWLIST);
        }
        String remaining = spec.trim();
        PidFilter.Mode mode = PidFilter.Mode.ALLOWLIST;
        if (remaining.startsWith("block:")) {
            mode = PidFilter.Mode.BLOCKLIST;
            remaining = remaining.substring(6);
        }
        boolean isTid = false;
        if (remaining.startsWith("tid:")) {
            isTid = true;
            remaining = remaining.substring(4);
        }
        List<Integer> ids = Arrays.stream(remaining.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        if (isTid) {
            return new PidFilterConfig(List.of(), ids, mode);
        } else {
            return new PidFilterConfig(ids, List.of(), mode);
        }
    }

    public PidFilter buildFilter() {
        PidFilter filter = new PidFilter();
        filter.setMode(mode);
        pids.forEach(filter::addPid);
        tids.forEach(filter::addTid);
        return filter;
    }

    public List<Integer> getPids() {
        return pids;
    }

    public List<Integer> getTids() {
        return tids;
    }

    public PidFilter.Mode getMode() {
        return mode;
    }
}

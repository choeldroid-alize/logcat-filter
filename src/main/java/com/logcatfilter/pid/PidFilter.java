package com.logcatfilter.pid;

import com.logcatfilter.parser.LogcatEntry;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Filters logcat entries by process ID (PID) or thread ID (TID).
 * Supports allowlist and blocklist modes.
 */
public class PidFilter {

    public enum Mode {
        ALLOWLIST,
        BLOCKLIST
    }

    private final Set<Integer> pids = new HashSet<>();
    private final Set<Integer> tids = new HashSet<>();
    private Mode mode;
    private boolean enabled;

    public PidFilter() {
        this.mode = Mode.ALLOWLIST;
        this.enabled = false;
    }

    public void addPid(int pid) {
        pids.add(pid);
        enabled = true;
    }

    public void removePid(int pid) {
        pids.remove(pid);
    }

    public void addTid(int tid) {
        tids.add(tid);
        enabled = true;
    }

    public void removeTid(int tid) {
        tids.remove(tid);
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Mode getMode() {
        return mode;
    }

    public boolean isEnabled() {
        return enabled && (!pids.isEmpty() || !tids.isEmpty());
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean accepts(LogcatEntry entry) {
        if (!isEnabled()) {
            return true;
        }
        boolean pidMatch = !pids.isEmpty() && pids.contains(entry.getPid());
        boolean tidMatch = !tids.isEmpty() && tids.contains(entry.getTid());
        boolean matched = pidMatch || tidMatch;
        return mode == Mode.ALLOWLIST ? matched : !matched;
    }

    public Set<Integer> getPids() {
        return Collections.unmodifiableSet(pids);
    }

    public Set<Integer> getTids() {
        return Collections.unmodifiableSet(tids);
    }

    public void clear() {
        pids.clear();
        tids.clear();
        enabled = false;
    }
}

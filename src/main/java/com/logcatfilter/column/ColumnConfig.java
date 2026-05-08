package com.logcatfilter.column;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Configuration for which columns to display and in what order.
 */
public class ColumnConfig {

    public enum Column {
        TIMESTAMP, PID, TID, LEVEL, TAG, MESSAGE
    }

    private final List<Column> visibleColumns;
    private final int timestampWidth;
    private final int tagWidth;
    private final int pidWidth;

    private ColumnConfig(Builder builder) {
        this.visibleColumns = Collections.unmodifiableList(new ArrayList<>(builder.visibleColumns));
        this.timestampWidth = builder.timestampWidth;
        this.tagWidth = builder.tagWidth;
        this.pidWidth = builder.pidWidth;
    }

    public List<Column> getVisibleColumns() {
        return visibleColumns;
    }

    public boolean isVisible(Column column) {
        return visibleColumns.contains(column);
    }

    public int getTimestampWidth() {
        return timestampWidth;
    }

    public int getTagWidth() {
        return tagWidth;
    }

    public int getPidWidth() {
        return pidWidth;
    }

    public static Builder defaultConfig() {
        return new Builder()
                .addColumn(Column.TIMESTAMP)
                .addColumn(Column.LEVEL)
                .addColumn(Column.TAG)
                .addColumn(Column.MESSAGE)
                .timestampWidth(19)
                .tagWidth(23)
                .pidWidth(6);
    }

    public static class Builder {
        private final List<Column> visibleColumns = new ArrayList<>();
        private int timestampWidth = 19;
        private int tagWidth = 23;
        private int pidWidth = 6;

        public Builder addColumn(Column column) {
            if (!visibleColumns.contains(column)) {
                visibleColumns.add(column);
            }
            return this;
        }

        public Builder timestampWidth(int width) {
            if (width < 1) throw new IllegalArgumentException("Width must be positive");
            this.timestampWidth = width;
            return this;
        }

        public Builder tagWidth(int width) {
            if (width < 1) throw new IllegalArgumentException("Width must be positive");
            this.tagWidth = width;
            return this;
        }

        public Builder pidWidth(int width) {
            if (width < 1) throw new IllegalArgumentException("Width must be positive");
            this.pidWidth = width;
            return this;
        }

        public ColumnConfig build() {
            if (visibleColumns.isEmpty()) {
                throw new IllegalStateException("At least one column must be visible");
            }
            return new ColumnConfig(this);
        }
    }
}

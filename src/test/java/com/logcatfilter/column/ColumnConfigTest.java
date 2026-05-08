package com.logcatfilter.column;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ColumnConfigTest {

    @Test
    void defaultConfigContainsExpectedColumns() {
        ColumnConfig config = ColumnConfig.defaultConfig().build();
        List<ColumnConfig.Column> cols = config.getVisibleColumns();
        assertTrue(cols.contains(ColumnConfig.Column.TIMESTAMP));
        assertTrue(cols.contains(ColumnConfig.Column.LEVEL));
        assertTrue(cols.contains(ColumnConfig.Column.TAG));
        assertTrue(cols.contains(ColumnConfig.Column.MESSAGE));
    }

    @Test
    void defaultConfigDoesNotContainPidByDefault() {
        ColumnConfig config = ColumnConfig.defaultConfig().build();
        assertFalse(config.isVisible(ColumnConfig.Column.PID));
    }

    @Test
    void customConfigRespectsColumnOrder() {
        ColumnConfig config = new ColumnConfig.Builder()
                .addColumn(ColumnConfig.Column.LEVEL)
                .addColumn(ColumnConfig.Column.MESSAGE)
                .build();
        List<ColumnConfig.Column> cols = config.getVisibleColumns();
        assertEquals(ColumnConfig.Column.LEVEL, cols.get(0));
        assertEquals(ColumnConfig.Column.MESSAGE, cols.get(1));
    }

    @Test
    void duplicateColumnIsIgnored() {
        ColumnConfig config = new ColumnConfig.Builder()
                .addColumn(ColumnConfig.Column.TAG)
                .addColumn(ColumnConfig.Column.TAG)
                .build();
        assertEquals(1, config.getVisibleColumns().size());
    }

    @Test
    void emptyColumnListThrows() {
        assertThrows(IllegalStateException.class, () -> new ColumnConfig.Builder().build());
    }

    @Test
    void invalidWidthThrows() {
        assertThrows(IllegalArgumentException.class, () ->
                new ColumnConfig.Builder()
                        .addColumn(ColumnConfig.Column.MESSAGE)
                        .tagWidth(0)
                        .build());
    }

    @Test
    void customWidthsAreStored() {
        ColumnConfig config = new ColumnConfig.Builder()
                .addColumn(ColumnConfig.Column.TAG)
                .tagWidth(15)
                .timestampWidth(12)
                .pidWidth(4)
                .build();
        assertEquals(15, config.getTagWidth());
        assertEquals(12, config.getTimestampWidth());
        assertEquals(4, config.getPidWidth());
    }
}

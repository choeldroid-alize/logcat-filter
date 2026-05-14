package com.logcatfilter.truncate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TruncationConfigTest {

    @Test
    void defaultConfig_hasExpectedDefaults() {
        TruncationConfig config = TruncationConfig.defaultConfig();

        assertEquals(TruncationConfig.DEFAULT_MAX_LENGTH, config.getMaxLength());
        assertEquals(TruncationConfig.DEFAULT_ELLIPSIS, config.getEllipsis());
        assertFalse(config.isTruncateTagField());
        assertTrue(config.isTruncateMessageField());
        assertTrue(config.isEnabled());
    }

    @Test
    void builder_customMaxLength_isApplied() {
        TruncationConfig config = TruncationConfig.builder()
                .maxLength(128)
                .build();

        assertEquals(128, config.getMaxLength());
    }

    @Test
    void builder_customEllipsis_isApplied() {
        TruncationConfig config = TruncationConfig.builder()
                .ellipsis("[truncated]")
                .build();

        assertEquals("[truncated]", config.getEllipsis());
    }

    @Test
    void builder_truncateTagFieldEnabled_isApplied() {
        TruncationConfig config = TruncationConfig.builder()
                .truncateTagField(true)
                .build();

        assertTrue(config.isTruncateTagField());
    }

    @Test
    void builder_truncateMessageFieldDisabled_isApplied() {
        TruncationConfig config = TruncationConfig.builder()
                .truncateMessageField(false)
                .build();

        assertFalse(config.isTruncateMessageField());
    }

    @Test
    void builder_disabledConfig_isApplied() {
        TruncationConfig config = TruncationConfig.builder()
                .enabled(false)
                .build();

        assertFalse(config.isEnabled());
    }

    @Test
    void builder_invalidMaxLength_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                TruncationConfig.builder().maxLength(0).build());

        assertThrows(IllegalArgumentException.class, () ->
                TruncationConfig.builder().maxLength(-10).build());
    }

    @Test
    void builder_nullEllipsis_throwsException() {
        assertThrows(IllegalArgumentException.class, () ->
                TruncationConfig.builder().ellipsis(null).build());
    }

    @Test
    void builder_emptyEllipsis_isAllowed() {
        TruncationConfig config = TruncationConfig.builder()
                .ellipsis("")
                .build();

        assertEquals("", config.getEllipsis());
    }
}

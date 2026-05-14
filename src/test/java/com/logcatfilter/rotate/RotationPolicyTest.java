package com.logcatfilter.rotate;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class RotationPolicyTest {

    @Test
    void defaultPolicyHasExpectedDefaults() {
        RotationPolicy policy = RotationPolicy.builder().build();
        assertEquals(10_000, policy.getMaxEntries());
        assertEquals(Duration.ofHours(1), policy.getMaxAge());
        assertEquals(RotationPolicy.TriggerMode.EITHER, policy.getTriggerMode());
        assertFalse(policy.isArchiveOnRotate());
    }

    @Test
    void shouldRotateBySizeWhenThresholdReached() {
        RotationPolicy policy = RotationPolicy.builder()
                .maxEntries(500)
                .triggerMode(RotationPolicy.TriggerMode.SIZE_ONLY)
                .build();
        assertFalse(policy.shouldRotateBySize(499));
        assertTrue(policy.shouldRotateBySize(500));
        assertTrue(policy.shouldRotateBySize(600));
    }

    @Test
    void shouldNotRotateBySizeWhenModeIsAgeOnly() {
        RotationPolicy policy = RotationPolicy.builder()
                .maxEntries(100)
                .triggerMode(RotationPolicy.TriggerMode.AGE_ONLY)
                .build();
        assertFalse(policy.shouldRotateBySize(9999));
    }

    @Test
    void shouldRotateByAgeWhenThresholdReached() {
        RotationPolicy policy = RotationPolicy.builder()
                .maxAge(Duration.ofMinutes(30))
                .triggerMode(RotationPolicy.TriggerMode.AGE_ONLY)
                .build();
        assertFalse(policy.shouldRotateByAge(Duration.ofMinutes(29)));
        assertTrue(policy.shouldRotateByAge(Duration.ofMinutes(30)));
        assertTrue(policy.shouldRotateByAge(Duration.ofMinutes(60)));
    }

    @Test
    void shouldNotRotateByAgeWhenModeIsSizeOnly() {
        RotationPolicy policy = RotationPolicy.builder()
                .maxAge(Duration.ofMinutes(5))
                .triggerMode(RotationPolicy.TriggerMode.SIZE_ONLY)
                .build();
        assertFalse(policy.shouldRotateByAge(Duration.ofHours(10)));
    }

    @Test
    void eitherModeTriggersByEitherCondition() {
        RotationPolicy policy = RotationPolicy.builder()
                .maxEntries(100)
                .maxAge(Duration.ofMinutes(10))
                .triggerMode(RotationPolicy.TriggerMode.EITHER)
                .build();
        assertTrue(policy.shouldRotate(100, Duration.ofMinutes(1)));
        assertTrue(policy.shouldRotate(10, Duration.ofMinutes(10)));
        assertFalse(policy.shouldRotate(50, Duration.ofMinutes(5)));
    }

    @Test
    void bothModeRequiresBothConditions() {
        RotationPolicy policy = RotationPolicy.builder()
                .maxEntries(100)
                .maxAge(Duration.ofMinutes(10))
                .triggerMode(RotationPolicy.TriggerMode.BOTH)
                .build();
        assertFalse(policy.shouldRotate(100, Duration.ofMinutes(5)));
        assertFalse(policy.shouldRotate(50, Duration.ofMinutes(10)));
        assertTrue(policy.shouldRotate(100, Duration.ofMinutes(10)));
    }

    @Test
    void archiveOnRotateFlagIsRespected() {
        RotationPolicy withArchive = RotationPolicy.builder().archiveOnRotate(true).build();
        RotationPolicy withoutArchive = RotationPolicy.builder().archiveOnRotate(false).build();
        assertTrue(withArchive.isArchiveOnRotate());
        assertFalse(withoutArchive.isArchiveOnRotate());
    }

    @Test
    void builderThrowsOnInvalidMaxEntries() {
        assertThrows(IllegalArgumentException.class, () ->
                RotationPolicy.builder().maxEntries(0).build());
        assertThrows(IllegalArgumentException.class, () ->
                RotationPolicy.builder().maxEntries(-1).build());
    }

    @Test
    void builderThrowsOnNullMaxAge() {
        assertThrows(NullPointerException.class, () ->
                RotationPolicy.builder().maxAge(null).build());
    }
}

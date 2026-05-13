package com.logcatfilter.profile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProfileManagerTest {

    private ProfileManager manager;

    @BeforeEach
    void setUp() {
        manager = new ProfileManager();
    }

    @Test
    void defaultProfileExists() {
        assertNotNull(manager.getActiveProfile());
        assertEquals("default", manager.getActiveProfileName());
    }

    @Test
    void createAndSwitchProfile() {
        manager.createProfile("work", "Work session filters");
        assertTrue(manager.switchProfile("work"));
        assertEquals("work", manager.getActiveProfileName());
    }

    @Test
    void createDuplicateProfileThrows() {
        manager.createProfile("dev", "Dev profile");
        assertThrows(IllegalArgumentException.class,
                () -> manager.createProfile("dev", "Another dev"));
    }

    @Test
    void switchToNonExistentProfileReturnsFalse() {
        assertFalse(manager.switchProfile("nonexistent"));
        assertEquals("default", manager.getActiveProfileName());
    }

    @Test
    void deleteProfileSwitchesBackToDefault() {
        manager.createProfile("temp", "Temp");
        manager.switchProfile("temp");
        assertTrue(manager.deleteProfile("temp"));
        assertEquals("default", manager.getActiveProfileName());
    }

    @Test
    void deleteDefaultProfileThrows() {
        assertThrows(IllegalStateException.class,
                () -> manager.deleteProfile("default"));
    }

    @Test
    void deleteNonExistentProfileReturnsFalse() {
        assertFalse(manager.deleteProfile("ghost"));
    }

    @Test
    void getProfileReturnsCorrectProfile() {
        manager.createProfile("qa", "QA filters");
        Optional<FilterProfile> profile = manager.getProfile("qa");
        assertTrue(profile.isPresent());
        assertEquals("qa", profile.get().getName());
    }

    @Test
    void listProfileNamesIncludesAllProfiles() {
        manager.createProfile("alpha", "");
        manager.createProfile("beta", "");
        List<String> names = manager.listProfileNames();
        assertTrue(names.contains("default"));
        assertTrue(names.contains("alpha"));
        assertTrue(names.contains("beta"));
        assertEquals(3, manager.getProfileCount());
    }

    @Test
    void profileSerializerRoundTrip() {
        ProfileSerializer serializer = new ProfileSerializer();
        FilterProfile profile = new FilterProfile("test", "Test desc");
        String serialized = serializer.serialize(profile);
        ProfileParseResult result = serializer.deserialize(serialized);
        assertTrue(result.isSuccess());
        assertEquals("test", result.getName());
        assertEquals("Test desc", result.getDescription());
    }

    @Test
    void profileSerializerFailsOnEmptyInput() {
        ProfileSerializer serializer = new ProfileSerializer();
        ProfileParseResult result = serializer.deserialize("");
        assertFalse(result.isSuccess());
        assertNotNull(result.getErrorMessage());
    }
}

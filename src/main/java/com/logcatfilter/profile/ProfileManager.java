package com.logcatfilter.profile;

import java.util.*;

/**
 * Manages named filter profiles: creation, switching, deletion, and listing.
 */
public class ProfileManager {

    private static final String DEFAULT_PROFILE_NAME = "default";

    private final Map<String, FilterProfile> profiles = new LinkedHashMap<>();
    private String activeProfileName;

    public ProfileManager() {
        FilterProfile defaultProfile = new FilterProfile(DEFAULT_PROFILE_NAME, "Default profile");
        profiles.put(DEFAULT_PROFILE_NAME, defaultProfile);
        activeProfileName = DEFAULT_PROFILE_NAME;
    }

    public FilterProfile getActiveProfile() {
        return profiles.get(activeProfileName);
    }

    public String getActiveProfileName() {
        return activeProfileName;
    }

    public boolean switchProfile(String name) {
        if (name == null || !profiles.containsKey(name.trim())) {
            return false;
        }
        activeProfileName = name.trim();
        return true;
    }

    public FilterProfile createProfile(String name, String description) {
        String trimmed = Objects.requireNonNull(name, "Name must not be null").trim();
        if (profiles.containsKey(trimmed)) {
            throw new IllegalArgumentException("Profile already exists: " + trimmed);
        }
        FilterProfile profile = new FilterProfile(trimmed, description);
        profiles.put(trimmed, profile);
        return profile;
    }

    public boolean deleteProfile(String name) {
        if (name == null) return false;
        String trimmed = name.trim();
        if (DEFAULT_PROFILE_NAME.equals(trimmed)) {
            throw new IllegalStateException("Cannot delete the default profile");
        }
        if (!profiles.containsKey(trimmed)) {
            return false;
        }
        profiles.remove(trimmed);
        if (activeProfileName.equals(trimmed)) {
            activeProfileName = DEFAULT_PROFILE_NAME;
        }
        return true;
    }

    public Optional<FilterProfile> getProfile(String name) {
        if (name == null) return Optional.empty();
        return Optional.ofNullable(profiles.get(name.trim()));
    }

    public List<String> listProfileNames() {
        return Collections.unmodifiableList(new ArrayList<>(profiles.keySet()));
    }

    public int getProfileCount() {
        return profiles.size();
    }
}

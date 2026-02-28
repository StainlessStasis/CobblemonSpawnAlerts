package io.github.stainlessstasis.config.manager;

import io.github.stainlessstasis.core.CobblemonSpawnAlerts;

import java.util.HashMap;
import java.util.Map;

public class VersionMatcher {
    private static String LAST_KNOWN_MOD_VERSION = "";
    public static final Map<String, String> MAJOR_CHANGES = new HashMap<>();
    static {
        MAJOR_CHANGES.put("1.12.0", "cobblemon-spawn-alerts.changelog.1_12_0");
    }

    /**
     * INTERNAL USE ONLY.
     * @param lastKnownModVersion The lowest version of the mod detected by the
     *                            {@link io.github.stainlessstasis.config.manager.AbstractConfigManager AbstractConfigManager}
     *                            (before it is automatically updated). Used for detecting when a user updates the mod to display a changelog.
     */
    static void setLastKnownModVersion(String lastKnownModVersion) {
        if (LAST_KNOWN_MOD_VERSION.isEmpty()) {
            LAST_KNOWN_MOD_VERSION = lastKnownModVersion;
            return;
        }

        var versionStatus = compareVersions(lastKnownModVersion, LAST_KNOWN_MOD_VERSION);
        if (versionStatus == VersionStatus.NEWER) {
            LAST_KNOWN_MOD_VERSION = lastKnownModVersion;
        }
    }

    public static String getLastKnownModVersion() {
        return LAST_KNOWN_MOD_VERSION;
    }

    /**
     * Compares the current mod version to target version
     * @return The status of the target version in relation to the current [NEWER, OLDER, NO_CHANGE]
     */
    public static VersionStatus compareVersions(String currentVersion, String targetVersion) {
        if (currentVersion.equals(targetVersion)) return VersionStatus.NO_CHANGE;

        String[] currentParts = currentVersion.split("\\.");
        String[] targetParts = targetVersion.split("\\.");
        int length = Math.max(currentParts.length, targetParts.length);

        for (int i = 0; i < length; i++) {
            int currentVal = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
            int targetVal = i < targetParts.length ? Integer.parseInt(targetParts[i]) : 0;

            if (targetVal > currentVal) return VersionStatus.NEWER;
            if (targetVal < currentVal) return VersionStatus.OLDER;
        }

        return VersionStatus.NO_CHANGE;
    }

    public enum VersionStatus {
        NEWER,
        OLDER,
        NO_CHANGE
    }

    /**
     * Returns whether the version has changed since the last time the game was started.
     */
    public static boolean hasVersionChanged() {
        var versionStatus = compareVersions(LAST_KNOWN_MOD_VERSION, CobblemonSpawnAlerts.MOD_VERSION);
        return versionStatus != VersionStatus.NO_CHANGE;
    }
}

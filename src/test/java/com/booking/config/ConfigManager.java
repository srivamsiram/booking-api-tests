package com.booking.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

/**
 * Central place for reading configuration.
 *
 * Lookup order (first non-empty value wins):
 *   1. JVM system property   e.g. mvn test -Dbase.url=http://localhost:3001
 *   2. Environment variable  e.g. BOOKER_BASE_URL (used by CI)
 *   3. config.properties     (default values committed to the repo)
 */
public final class ConfigManager {

    private static final String CONFIG_FILE = "config.properties";
    private static final String ENV_PREFIX = "BOOKER_";
    private static final Properties PROPERTIES = loadProperties();

    private ConfigManager() {
        // Utility class: no instances
    }

    public static String getBaseUrl() {
        return get("base.url");
    }

    public static String getUsername() {
        return get("username");
    }

    public static String getPassword() {
        return get("password");
    }

    public static String get(String key) {
        String systemValue = System.getProperty(key);
        if (isSet(systemValue)) {
            return systemValue.trim();
        }

        String envValue = System.getenv(toEnvName(key));
        if (isSet(envValue)) {
            return envValue.trim();
        }

        String fileValue = PROPERTIES.getProperty(key);
        if (isSet(fileValue)) {
            return fileValue.trim();
        }

        throw new IllegalStateException("Missing config value for '" + key
                + "'. Set it in " + CONFIG_FILE + ", as -D" + key
                + ", or as env var " + toEnvName(key));
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load " + CONFIG_FILE, e);
        }
        return properties;
    }

    // "base.url" -> "BOOKER_BASE_URL"
    private static String toEnvName(String key) {
        return ENV_PREFIX + key.toUpperCase().replace('.', '_');
    }

    private static boolean isSet(String value) {
        return value != null && !value.isBlank();
    }
}
package com.ecommerce.api.config;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {

    private static final Properties properties = new Properties();
    private static final Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .ignoreIfMalformed()
            .load();

    static {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties not found on classpath");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private ConfigManager() {
    }

    public static String getBaseUrl() {
        String value = getSecret("BASE_URL");
        if (value == null || value.isBlank()) {
            value = properties.getProperty("base.url");
        }
        return value;
    }

    public static String getDealSlug() {
        return properties.getProperty("deal.slug");
    }

    public static String getInvalidDealSlug() {
        return properties.getProperty("deal.invalid.slug");
    }

    public static String getApiKey() {
        return getSecret("API_KEY");
    }

    public static String getApiSecretKey() {
        return getSecret("API_SECRET_KEY");
    }

    /**
     * Looks up a one-off test fixture value (a tracking number, reserve id, etc.) by its
     * config.properties key. These are single-use-per-test values, so they're kept as plain
     * key lookups instead of one bespoke getter per fixture.
     */
    public static String getFixture(String key) {
        return properties.getProperty(key);
    }

    /**
     * Real environment variables take priority over .env so CI secrets (which are
     * injected as env vars, not files) override local development values.
     */
    private static String getSecret(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            value = dotenv.get(name);
        }
        return value;
    }
}

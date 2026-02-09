package com.homey.htaf.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public class Settings {
    private static final String DEFAULT_ENV = "qa";

    private final String baseUrl;
    private final String username;
    private final String password;
    private final String mailtrapToken;
    private final String mailtrapInboxId;

    public Settings() {
        String env = System.getProperty("env", DEFAULT_ENV).toLowerCase(Locale.ROOT);
        String resourcePath = String.format("env/%s.properties", env);

        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IllegalStateException("Env properties not found: " + resourcePath);
            }
            properties.load(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load env properties: " + resourcePath, e);
        }

        String baseUrlOverride = System.getProperty("baseUrl");
        this.baseUrl = baseUrlOverride != null && !baseUrlOverride.isBlank()
                ? baseUrlOverride
                : properties.getProperty("baseUrl", "");
        this.username = properties.getProperty("username", "");
        this.password = properties.getProperty("password", "");
        this.mailtrapToken = resolveSecret("mailtrapToken", "MAILTRAP_API_TOKEN", properties);
        this.mailtrapInboxId = resolveSecret("mailtrapInboxId", "MAILTRAP_INBOX_ID", properties);
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getMailtrapToken() {
        return mailtrapToken;
    }

    public String getMailtrapInboxId() {
        return mailtrapInboxId;
    }

    private String resolveSecret(String propertyKey, String envKey, Properties properties) {
        String override = System.getProperty(propertyKey);
        if (override != null && !override.isBlank()) {
            return override;
        }
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        return properties.getProperty(propertyKey, "");
    }
}

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
}

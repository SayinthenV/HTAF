package com.homey.htaf.hooks;

import com.homey.htaf.config.Settings;
import com.homey.htaf.drivers.PlaywrightManager;
import io.cucumber.java.After;
import io.cucumber.java.Before;

public class Hooks {
    private final Settings settings = new Settings();
    private final PlaywrightManager playwrightManager = new PlaywrightManager();

    @Before
    public void beforeScenario() {
        playwrightManager.init();
        String baseUrl = settings.getBaseUrl();
        if (baseUrl != null && !baseUrl.isBlank()) {
            playwrightManager.getPage().navigate(baseUrl);
        }
    }

    @After
    public void afterScenario() {
        playwrightManager.close();
    }
}

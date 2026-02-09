package com.homey.htaf.hooks;

import com.homey.htaf.config.Settings;
import com.homey.htaf.core.ScenarioContext;
import com.homey.htaf.drivers.PlaywrightManager;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitUntilState;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;

public class Hooks {
    private final Settings settings = new Settings();
    private final PlaywrightManager playwrightManager = PlaywrightManager.getInstance();

    @Before
    public void beforeScenario() {
        playwrightManager.init();
        String baseUrl = settings.getBaseUrl();
        if (baseUrl != null && !baseUrl.isBlank()) {
            playwrightManager.getPage().navigate(
                    baseUrl,
                    new com.microsoft.playwright.Page.NavigateOptions()
                            .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
                            .setTimeout(60000)
            );
        } else {
            throw new IllegalStateException("baseUrl is blank. Set it in env properties or via -DbaseUrl.");
        }
    }

    @After
    public void afterScenario(Scenario scenario) {
        Page page = playwrightManager.getPage();
        if (scenario.isFailed() && page != null) {
            try {
                byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                Allure.addAttachment("failure-screenshot", new ByteArrayInputStream(screenshot));
            } catch (RuntimeException ignored) {
                // Don't block teardown on screenshot failures.
            }
        }
        ScenarioContext.getInstance().clear();
        playwrightManager.close();
    }
}

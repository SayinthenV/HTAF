package com.homey.htaf.drivers;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class PlaywrightManager {
    private static final PlaywrightManager INSTANCE = new PlaywrightManager();
    private static final boolean DEFAULT_HEADLESS = false;
    private static final int DEFAULT_TIMEOUT_MS = 30000;

    private static final ThreadLocal<Playwright> PLAYWRIGHT = new ThreadLocal<>();
    private static final ThreadLocal<Browser> BROWSER = new ThreadLocal<>();
    private static final ThreadLocal<Page> PAGE = new ThreadLocal<>();

    public static PlaywrightManager getInstance() {
        return INSTANCE;
    }

    public void init() {
        if (PAGE.get() != null) {
            return;
        }

        boolean headless = Boolean.parseBoolean(System.getProperty("headless", String.valueOf(DEFAULT_HEADLESS)));
        int timeoutMs = parseTimeout(System.getProperty("timeout"), DEFAULT_TIMEOUT_MS);

        Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(headless)
        );
        Page page = browser.newPage();
        page.setDefaultTimeout(timeoutMs);
        page.setDefaultNavigationTimeout(timeoutMs);

        PLAYWRIGHT.set(playwright);
        BROWSER.set(browser);
        PAGE.set(page);
    }

    public Page getPage() {
        return PAGE.get();
    }

    public Browser getBrowser() {
        return BROWSER.get();
    }

    public void close() {
        Page page = PAGE.get();
        if (page != null) {
            page.close();
            PAGE.remove();
        }
        Browser browser = BROWSER.get();
        if (browser != null) {
            browser.close();
            BROWSER.remove();
        }
        Playwright playwright = PLAYWRIGHT.get();
        if (playwright != null) {
            playwright.close();
            PLAYWRIGHT.remove();
        }
    }

    private int parseTimeout(String value, int defaultValue) {
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("Invalid timeout value '" + value + "', using default " + defaultValue + "ms.");
            return defaultValue;
        }
    }
}

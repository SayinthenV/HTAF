package com.homey.htaf.core;

import com.homey.htaf.drivers.PlaywrightManager;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public abstract class BasePage {
    protected final Page page;

    protected BasePage() {
        this.page = new PlaywrightManager().getPage();
    }

    protected void click(String selector) {
        page.click(selector);
    }

    protected void fill(String selector, String value) {
        page.fill(selector, value);
    }

    protected void waitVisible(String selector) {
        page.waitForSelector(
                selector,
                new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE)
        );
    }

    protected String getText(String selector) {
        return page.textContent(selector);
    }
}

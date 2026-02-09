package com.homey.htaf.pages;

import com.homey.htaf.config.Settings;
import com.homey.htaf.core.BasePage;

public class LoginPage extends BasePage {
    private static final String EMAIL_TAB = "a.auth-tabs--tab[href='/auth']";
    private static final String PHONE_TAB = "a.auth-tabs--tab[href='/auth?identifier_type=phone_number']";
    private static final String FORGOT_PASSWORD_LINK = ".auth-sub-heading a[href='/users/password/new']";
    private static final String USERNAME_INPUT = "#user_authentication_service_identifier";
    private static final String PASSWORD_INPUT = "#user_authentication_service_password";
    private static final String SUBMIT_BUTTON = "input[type='submit'][value='Continue']";
    private static final String SETUP_AUTH_HEADING = "h1.auth-heading";
    private static final String SKIP_FOR_NOW = "a.w-full.button.button--secondary[href='/auth/tokens']";
    private static final String ERROR_CONTAINER = ".alert-component";
    private static final String ERROR_HEADER = ".alert-component h3";
    private static final String ERROR_TEXT = ".alert-component p";

    private final Settings settings;

    public LoginPage(Settings settings) {
        super();
        this.settings = settings;
    }

    public void navigate() {
        page.navigate(settings.getBaseUrl());
    }

    public void login(String username, String password) {
        clickTabForIdentifier(username);
        waitVisible(USERNAME_INPUT);
        fill(USERNAME_INPUT, username);
        fill(PASSWORD_INPUT, password);
        click(SUBMIT_BUTTON);
        skipAuthenticatorIfPrompted();
    }

    public void openForgotPassword() {
        waitVisible(FORGOT_PASSWORD_LINK);
        click(FORGOT_PASSWORD_LINK);
        page.waitForURL("**/users/password/new**");
    }

    public boolean isLoggedIn(String identifier) {
        page.waitForLoadState();
        if (isLikelyPhone(identifier)) {
            return !page.isVisible(USERNAME_INPUT) && !page.isVisible(PASSWORD_INPUT);
        }
        try {
            page.waitForFunction("() => document.title.includes('Referrals | Homey Backoffice')",
                    null,
                    new com.microsoft.playwright.Page.WaitForFunctionOptions().setTimeout(10000));
        } catch (com.microsoft.playwright.TimeoutError ignored) {
            // Fall through to a simple check.
        }
        String title = page.title();
        return title != null && title.contains("Referrals | Homey Backoffice");
    }

    public boolean isLoggedInByFormState() {
        page.waitForLoadState();
        if (page.isVisible(ERROR_CONTAINER)) {
            return false;
        }
        return !page.isVisible(USERNAME_INPUT) && !page.isVisible(PASSWORD_INPUT);
    }

    public String getErrorMessage() {
        waitVisible(ERROR_CONTAINER);
        String header = getText(ERROR_HEADER);
        String text = getText(ERROR_TEXT);
        if (header == null) {
            header = "";
        }
        if (text == null) {
            text = "";
        }
        return header.trim() + "\n" + text.trim();
    }

    private void clickEmailTabIfPresent() {
        if (page.isVisible(EMAIL_TAB)) {
            click(EMAIL_TAB);
        }
    }

    private void clickTabForIdentifier(String identifier) {
        if (isLikelyPhone(identifier) && page.isVisible(PHONE_TAB)) {
            click(PHONE_TAB);
            return;
        }
        clickEmailTabIfPresent();
    }

    private boolean isLikelyPhone(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return false;
        }
        String trimmed = identifier.trim();
        return trimmed.startsWith("+") || trimmed.matches("\\d{6,}");
    }

    private void skipAuthenticatorIfPrompted() {
        if (!page.isVisible(SETUP_AUTH_HEADING)) {
            return;
        }
        String heading = page.textContent(SETUP_AUTH_HEADING);
        if (heading != null && heading.trim().equalsIgnoreCase("Set up your authenticator app")) {
            if (page.isVisible(SKIP_FOR_NOW)) {
                click(SKIP_FOR_NOW);
            }
        }
    }
}

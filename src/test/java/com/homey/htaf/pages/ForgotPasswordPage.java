package com.homey.htaf.pages;

import com.homey.htaf.core.BasePage;

public class ForgotPasswordPage extends BasePage {
    private static final String EMAIL_TAB = "a.auth-tabs--tab[href*='identifier_type=email_address']";
    private static final String IDENTIFIER_INPUT = "input[name$='[identifier]']";
    private static final String SUBMIT_BUTTON = "input[type='submit'][value='Continue'], button[type='submit']";
    private static final String HEADING = "h1.auth-heading";
    private static final String PASSWORD_INPUT = "input#user_authentication_service_password";
    private static final String PASSWORD_CONFIRM_INPUT = "input#user_authentication_service_password_confirmation";
    private static final String CREATE_PASSWORD_BUTTON = "input[type='submit'][value='Create Password'], button[type='submit']";
    private static final String SETUP_AUTH_HEADING = "h1.auth-heading";
    private static final String SKIP_FOR_NOW = "a.w-full.button.button--secondary[href='/auth/tokens']";
    private static final String ALERT_CONTAINER = ".alert-component";
    private static final String ALERT_HEADER = ".alert-component h3";
    private static final String ALERT_TEXT = ".alert-component p";
    private static final String LOGIN_USERNAME_INPUT = "#user_authentication_service_identifier";
    private static final String LOGIN_PASSWORD_INPUT = "#user_authentication_service_password";

    public void requestResetForEmail(String email) {
        waitVisible(HEADING);
        if (page.isVisible(EMAIL_TAB)) {
            click(EMAIL_TAB);
        }
        waitVisible(IDENTIFIER_INPUT);
        fill(IDENTIFIER_INPUT, email);
        click(SUBMIT_BUTTON);
    }

    public boolean isResetNoticeDisplayedForEmail(String email) {
        String headingText = "Reset link sent";
        String expectedPrefix = "We've sent a reset link to";
        try {
            waitVisible("h1.auth-heading");
            String heading = page.textContent("h1.auth-heading");
            if (heading == null || !heading.trim().equalsIgnoreCase(headingText)) {
                return false;
            }
            waitVisible("text=" + expectedPrefix);
            if (!page.isVisible("text=" + expectedPrefix)) {
                return false;
            }
        } catch (com.microsoft.playwright.TimeoutError ignored) {
            return false;
        }
        return true;
    }

    public void openResetLink(String url) {
        page.navigate(url);
        page.waitForURL("**/password/edit**");
    }

    public boolean isResetPasswordPage() {
        if (!page.isVisible(HEADING)) {
            return false;
        }
        String text = page.textContent(HEADING);
        if (text == null) {
            return false;
        }
        String heading = text.trim();
        return heading.equalsIgnoreCase("Reset Password")
                || heading.equalsIgnoreCase("Create new password");
    }

    public void createNewPassword(String password) {
        waitVisible(PASSWORD_INPUT);
        fill(PASSWORD_INPUT, password);
        waitVisible(PASSWORD_CONFIRM_INPUT);
        fill(PASSWORD_CONFIRM_INPUT, password);
        click(CREATE_PASSWORD_BUTTON);
        skipAuthenticatorIfPrompted();
        waitForPostResetLanding();
    }

    public void createNewPasswordWithConfirmation(String password, String confirmPassword) {
        waitVisible(PASSWORD_INPUT);
        fill(PASSWORD_INPUT, password);
        waitVisible(PASSWORD_CONFIRM_INPUT);
        fill(PASSWORD_CONFIRM_INPUT, confirmPassword);
        click(CREATE_PASSWORD_BUTTON);
    }

    public String getResetPasswordErrorMessage() {
        waitVisible(ALERT_CONTAINER);
        String header = getText(ALERT_HEADER);
        String text = getText(ALERT_TEXT);
        if (header == null) {
            header = "";
        }
        if (text == null) {
            text = "";
        }
        return header.trim() + "\n" + text.trim();
    }

    private void skipAuthenticatorIfPrompted() {
        if (page.isVisible(SETUP_AUTH_HEADING)) {
            String heading = page.textContent(SETUP_AUTH_HEADING);
            if (heading != null && heading.trim().equalsIgnoreCase("Set up your authenticator app")) {
                if (page.isVisible(SKIP_FOR_NOW)) {
                    click(SKIP_FOR_NOW);
                    return;
                }
            }
        }
        if (page.isVisible(SKIP_FOR_NOW)) {
            click(SKIP_FOR_NOW);
        }
    }

    private void waitForPostResetLanding() {
        page.waitForFunction(
                "() => {" +
                        "const body = document.body;" +
                        "if (!body) return false;" +
                        "const text = body.innerText || '';" +
                        "if (text.includes('Dashboard')) return true;" +
                        "const user = document.querySelector('" + LOGIN_USERNAME_INPUT + "');" +
                        "const pass = document.querySelector('" + LOGIN_PASSWORD_INPUT + "');" +
                        "return !!(user && pass);" +
                        "}",
                null,
                new com.microsoft.playwright.Page.WaitForFunctionOptions().setTimeout(60000)
        );
    }

}

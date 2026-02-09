package com.homey.htaf.steps;

import com.homey.htaf.config.Settings;
import com.homey.htaf.core.BaseStep;
import com.homey.htaf.core.ScenarioContext;
import com.homey.htaf.pages.ForgotPasswordPage;
import com.homey.htaf.pages.LoginPage;
import com.homey.htaf.utils.MailtrapClient;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class ForgotPasswordSteps extends BaseStep {
    private final LoginPage loginPage;
    private final ForgotPasswordPage forgotPasswordPage;
    private final Settings settings;

    public ForgotPasswordSteps() {
        super(new ScenarioContext());
        this.settings = new Settings();
        this.loginPage = new LoginPage(settings);
        this.forgotPasswordPage = new ForgotPasswordPage();
    }

    @When("I request a password reset for {string}")
    public void requestPasswordReset(String email) {
        loginPage.openForgotPassword();
        forgotPasswordPage.requestResetForEmail(email);
        context.set("resetEmail", email);
    }

    @Then("I should see reset link sent notice")
    public void shouldSeeResetLinkSentNotice() {
        String email = context.get("resetEmail", String.class);
        Assert.assertTrue(
                forgotPasswordPage.isResetNoticeDisplayedForEmail(email),
                "Reset link sent notice not displayed."
        );
    }

    @Then("I open the password reset link from Mailtrap")
    public void openResetLinkFromMailtrap() {
        String email = context.get("resetEmail", String.class);
        MailtrapClient client = new MailtrapClient(settings.getMailtrapToken(), settings.getMailtrapInboxId());
        String link = client.waitForResetLink(email, System.getProperty("mailtrapSubject"));
        context.set("resetLink", link);
        forgotPasswordPage.openResetLink(link);
    }

    @Then("I should see the reset password page")
    public void shouldSeeResetPasswordPage() {
        Assert.assertTrue(forgotPasswordPage.isResetPasswordPage(), "Reset password page not displayed.");
    }

    @When("I create a new password {string}")
    public void createNewPassword(String password) {
        context.set("resetPassword", password);
        forgotPasswordPage.createNewPassword(password);
    }

    @Then("I login with the reset password")
    public void loginWithResetPassword() {
        String email = context.get("resetEmail", String.class);
        String password = context.get("resetPassword", String.class);
        loginPage.navigate();
        loginPage.login(email, password);
        context.set("loggedInUser", email);
        Assert.assertTrue(loginPage.isLoggedInByFormState(), "Expected user to be logged in with reset password.");
    }
}

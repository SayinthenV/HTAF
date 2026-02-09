package com.homey.htaf.steps;

import com.homey.htaf.config.Settings;
import com.homey.htaf.core.BaseStep;
import com.homey.htaf.core.ScenarioContext;
import com.homey.htaf.pages.LoginPage;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.Map;

public class LoginSteps extends BaseStep {
    private final Settings settings;
    private final LoginPage loginPage;

    public LoginSteps() {
        super(new ScenarioContext());
        this.settings = new Settings();
        this.loginPage = new LoginPage(settings);
    }

    @Given("I login as default user")
    public void loginAsDefaultUser() {
        loginPage.navigate();
        loginPage.login(settings.getUsername(), settings.getPassword());
        context.set("loggedInUser", settings.getUsername());
    }

    @Given("I am on the login page")
    public void onLoginPage() {
        loginPage.navigate();
    }

    @When("I login with credentials:")
    public void loginWithCredentials(DataTable dataTable) {
        Map<String, String> credentials = dataTable.asMap(String.class, String.class);
        String username = credentials.getOrDefault("username", "");
        String password = credentials.getOrDefault("password", "");
        loginPage.login(username, password);
        context.set("loggedInUser", username);
    }

    @Then("I should be logged in")
    public void shouldBeLoggedIn() {
        String identifier = context.get("loggedInUser", String.class);
        Assert.assertTrue(loginPage.isLoggedIn(identifier), "Expected user to be logged in.");
    }

    @Then("I should see error message:")
    public void shouldSeeErrorMessage(String expectedMessage) {
        String actual = normalize(loginPage.getErrorMessage());
        String expected = normalize(expectedMessage);
        if (actual.equals(expected)) {
            return;
        }
        if (isKnownLoginErrorVariant(actual) && isKnownLoginErrorVariant(expected)) {
            return;
        }
        Assert.assertEquals(actual, expected, "Error message mismatch.");
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\r\n", "\n").trim();
    }

    private boolean isKnownLoginErrorVariant(String message) {
        String header = "Incorrect login details";
        String variantA = "The email or password are invalid. Please make sure your details are correct or try to log-in via your mobile number instead.";
        String variantB = "If you're a new user or forgot your password, click here to create a new one.";
        return message.equals(header + "\n" + variantA) || message.equals(header + "\n" + variantB);
    }
}

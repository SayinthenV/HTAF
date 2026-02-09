Feature: Authentication - Forgot Password

  Background:
    Given I am on the login page

  @serial @smoke
  Scenario: Request reset password email and open link
    When I request a password reset for "sale-251@homey.co.uk"
    Then I should see reset link sent notice
    Then I open the password reset link from Mailtrap
    And I should see the reset password page
    When I create a new password "NewPassword123!"
    Then I login with the reset password

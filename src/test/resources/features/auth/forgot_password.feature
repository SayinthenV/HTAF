Feature: Authentication - Forgot Password

  Background:
    Given I am on the login page

  @smoke
  Scenario: Request reset password email and open link
    When I request a password reset for "sale-251@homey.co.uk"
    Then I should see reset link sent notice
    Then I open the password reset link from Mailtrap
    And I should see the reset password page
    When I create a new password "NewPassword12345!"
    Then I login with the reset password

  @smoke @negative
  Scenario: Reset password with mismatched confirmation shows error
    When I request a password reset for "sale-251@homey.co.uk"
    Then I should see reset link sent notice
    Then I open the password reset link from Mailtrap
    And I should see the reset password page
    When I create a new password "NewPassword12345!" and confirm "NewPassword12345!X"
    Then I should see reset password error message:
      """
      Invalid password
      Your passwords don't match. Please make sure your 'Password' and 'Password confirmation' are the same.
      """

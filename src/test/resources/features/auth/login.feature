Feature: Authentication - Login

  Background:
    Given I am on the login page

  @parallel @smoke @happy
  Scenario: Login with email succeeds
    When I login with credentials:
      | username | admin-16@homey.co.uk |
      | password | xhqa93wQM6L229L4WaBil239 |
    Then I should be logged in

  @parallel @smoke @happy
  Scenario: Login with mobile succeeds
    When I login with credentials:
      | username | 2078936401 |
      | password | xhqa93wQM6L229L4WaBil239 |
    Then I should be logged in

  @smoke @negative
  Scenario: Login with unknown account shows error
    When I login with credentials:
      | username | wrong@example.com |
      | password | wrongPassword |
    Then I should see error message:
      """
      Incorrect login details
      The email or password are invalid. Please make sure your details are correct or try to log-in via your mobile number instead.
      """

  @smoke @negative
  Scenario: Login with wrong password shows error
    When I login with credentials:
      | username | admin-16@homey.co.uk |
      | password | wrongPassword |
    Then I should see error message:
      """
      Incorrect login details
      If you're a new user or forgot your password, click here to create a new one.
      """
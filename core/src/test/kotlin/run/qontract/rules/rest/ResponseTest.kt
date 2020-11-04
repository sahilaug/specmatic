package run.qontract.rules.rest

import org.junit.jupiter.api.Test
import run.qontract.backwardCompatibleWith
import run.qontract.notBackwardCompatibleWith

class ResponseTest {
    @Test
    fun `adding an optional key to the response body is backward compatible`() {
        val newContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | name | (string) |
    And json Status
    | status | (string) |
    | data?  | (string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
""".trimIndent()

        newContract backwardCompatibleWith oldContract
    }

    @Test
    fun `adding non-optional key to the response body is not backward compatible`() {
        // TODO: VALID, NOT PASSING, MAKE THIS PASS

        val newContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | name    | (string) |
    And json Status
    | status | (string) |
    | data  | (string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
""".trimIndent()

        newContract notBackwardCompatibleWith oldContract
    }

    @Test
    fun `removing non-optional key to the response body is not backward compatible`() {
        val oldContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | name | (string) |
    And json Status
    | status | (string) |
    | data  | (string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
""".trimIndent()

        val newContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | name    | (string) |
    And json Status
    | status | (string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
""".trimIndent()

        newContract notBackwardCompatibleWith oldContract
    }

    @Test
    fun `making the response body type optional is not backward compatible`() {
        val oldContract = """
Feature: Update value API
  Scenario: Update value
    When POST /user/(key:number)
    And request-body (number)
    Then status 200
    And response-body (number)
""".trimIndent()

        val newContract = """
Feature: Update value API
  Scenario: Update value
    When POST /user/(key:number)
    And request-body (number)
    Then status 200
    And response-body (number?)
""".trimIndent()

        newContract notBackwardCompatibleWith oldContract
    }

    @Test
    fun `making a json value's type optional in response body is not backward compatible`() {
        val newContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | name | (string) |
    And json Status
    | status | (string?) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
""".trimIndent()

        newContract notBackwardCompatibleWith oldContract
    }

    @Test
    fun `making a optional json value's type non-optional in response body is not backward compatible`() {
        // TODO: NOT PASSING, NOT BACKWARD COMPATIBLE, MAKE THIS PASS

        val oldContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | name | (string) |
    And json Status
    | status | (string?) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
""".trimIndent()

        val newContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | name | (string) |
    And json Status
    | status | (string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
""".trimIndent()

        newContract notBackwardCompatibleWith oldContract
    }

    @Test
    fun `change number in string to string in json value in response body is not backward compatible`() {
        val oldContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | id | (string) |
    And json Status
    | status | (number in string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
        """.trimIndent()

        val newContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | id | (string) |
    And json Status
    | status | (string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
""".trimIndent()

        newContract notBackwardCompatibleWith oldContract
    }

    @Test
    fun `change from string to number in string in response body is not backward compatible`() {
        // TODO: SHOULD BE BACKWARD COMPATIBLE

        val oldContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | id | (string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (string)
        """.trimIndent()

        val newContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | id | (number in string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (number in string)
""".trimIndent()

        newContract notBackwardCompatibleWith oldContract
    }

    @Test
    fun `changing a key from optional to non optional in the response body is not backward compatible`() {
        val oldContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | id | (string) |
    And json Status
    | status | (string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
        """.trimIndent()

        val newContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | id | (string) |
    And json Status
    | status? | (string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
        """.trimIndent()

        newContract notBackwardCompatibleWith oldContract
    }

    @Test
    fun `removing a non-optional key in the response body is not backward compatible`() {
        val oldContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | id   | (string)  |
    And json Status
    | status | (string) |
    | data   | (string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
        """.trimIndent()

        val newContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | id | (string) |
    And json Status
    | status | (string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
        """.trimIndent()

        newContract notBackwardCompatibleWith oldContract
    }
}
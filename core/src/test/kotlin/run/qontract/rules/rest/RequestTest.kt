package run.qontract.rules.rest

import org.junit.jupiter.api.Test
import run.qontract.backwardCompatibleWith
import run.qontract.notBackwardCompatibleWith

internal val oldContract = """
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

class RequestTest {
    @Test
    fun `adding non-optional key to the request body is backward compatible`() {
        val newContract = """
Feature: User API
Scenario: Add user
Given json User
| name    | (string) |
| address | (string) |
And json Status
| status | (string) |
When POST /user
And request-body (User)
Then status 200
And response-body (Status)
""".trimIndent()

        newContract backwardCompatibleWith oldContract
    }

    @Test
    fun `removing non-optional key in the request body is not backward compatible`() {
        val newContract = """
Feature: User API
Scenario: Add user
Given json User
| address | (string) |
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
    fun `adding optional key in the request body is backward compatible`() {
        val newContract = """
Feature: User API
Scenario: Add user
Given json User
| name     | (string) |
| address? | (string) |
And json Status
| status | (string) |
When POST /user
And request-body (User)
Then status 200
And response-body (Status)
""".trimIndent()

        newContract backwardCompatibleWith oldContract
    }

    @Test
    fun `making the request body type optional is not backward compatible`() {
        val oldContract = """
Feature: Update value API
  Scenario: Update value
    And json Status
    | status | (string) |
    When POST /user/(key:number)
    And request-body (number)
    Then status 200
    And response-body (Status)
""".trimIndent()

        val newContract = """
Feature: Update value API
  Scenario: Update value
    And json Status
    | status | (string) |
    When POST /user/(key:number)
    And request-body (number?)
    Then status 200
    And response-body (Status)
""".trimIndent()

        newContract notBackwardCompatibleWith oldContract
    }

    @Test
    fun `making a json value's type optional in request body is not backward compatible`() {
        val newContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | name     | (string?) |
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
    fun `changing the request payload type from number to string is not backward compatible`() {
        val oldContract = """
Feature: User API
  Scenario: Add user
    And json Status
    | status | (string) |
    When POST /user
    And request-body (number)
    Then status 200
    And response-body (Status)
        """.trimIndent()

        val newContract = """
Feature: User API
  Scenario: Add user
    And json Status
    | status | (string) |
    When POST /user
    And request-body (string)
    Then status 200
    And response-body (Status)
        """.trimIndent()

        newContract notBackwardCompatibleWith oldContract
    }

    @Test
    fun `change number in string to string in request body is not backward compatible`() {
        val oldContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | id | (number in string) |
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
    | status | (string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
""".trimIndent()

        newContract notBackwardCompatibleWith oldContract
    }

    @Test
    fun `change from string to number in string in request body is backward compatible`() {
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
    | id | (number in string) |
    And json Status
    | status | (string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
""".trimIndent()

        newContract backwardCompatibleWith oldContract
    }

    @Test
    fun `changing a key from optional to non optional in the request body is backward compatible`() {
        val oldContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | id | (string?) |
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
    | status | (string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
        """.trimIndent()

        newContract backwardCompatibleWith oldContract
    }

    @Test
    fun `removing an optional key in the request body is backward compatible`() {
        val oldContract = """
Feature: User API
  Scenario: Add user
    Given json User
    | id   | (string)  |
    | name | (string?) |
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
    | status | (string) |
    When POST /user
    And request-body (User)
    Then status 200
    And response-body (Status)
        """.trimIndent()

        newContract backwardCompatibleWith oldContract
    }
}
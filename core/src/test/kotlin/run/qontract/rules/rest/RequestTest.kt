package run.qontract.rules.rest

import org.junit.jupiter.api.Disabled
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
    fun `adding non-optional key to the request body is not backward compatible`() {
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

        newContract notBackwardCompatibleWith oldContract
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
    fun `making the request body type optional is backward compatible`() {
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

        newContract backwardCompatibleWith oldContract
    }

    @Test
    fun `making a json value's type optional in request body is backward compatible`() {
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

        newContract backwardCompatibleWith oldContract
    }

    @Disabled
    @Test
    fun `changing the request payload type from number to string is backward compatible`() {
        // TODO: GRAY AREA, needs further thought
        // Issues:
        // 1. Changing from number to string should not be an issue for provider unmarshallers,
        //    and ideally should not matter to the consumer either, which continues to send a number,
        //    which the provider now interprets as a string
        // 2. But changing from string to number might be an issue for provider unmarshallers,
        //    which are used to seeing a number and will break when trying to convert an alphanumeric string to
        //    a number, if this change happens unexpectedly
        // Possible approach to address this:
        // 1. Consider the change backward incompatible, and let the proposer add a new scenario
        //    with the new request body type.
        // * This requires some work on Qontract.
        //
        // Related issues:
        // 1. Changing from number to string in the body might not be support

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

        newContract backwardCompatibleWith oldContract
    }

    @Test
    fun `change number in string to string in request body JSON is backward compatible`() {
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

        newContract backwardCompatibleWith oldContract
    }

    @Test
    fun `change from string to number in string in request body is backward compatible`() {
        // TODO MAKE THIS PASS
        // Consumer will not have to change, provider is anyway expecting a string.
        // Changing from string to number in string will not require either to change.

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
    fun `changing a key from optional to non optional in the request body is not backward compatible`() {
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

        newContract notBackwardCompatibleWith oldContract
    }

    @Test
    fun `removing an optional key in the request body is not backward compatible`() {
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

        newContract notBackwardCompatibleWith oldContract
    }
}
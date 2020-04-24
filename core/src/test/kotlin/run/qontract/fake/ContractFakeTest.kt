package run.qontract.fake

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.fail
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.postForEntity
import run.qontract.core.HttpRequest
import run.qontract.core.HttpResponse
import run.qontract.core.pattern.parsedPattern
import run.qontract.core.pattern.parsedValue
import run.qontract.core.value.JSONObjectValue
import run.qontract.core.value.NumberValue
import run.qontract.core.value.StringValue
import run.qontract.mock.MockScenario

internal class ContractFakeTest {
    @Test
    fun `should serve mocked data before stub`() {
        val gherkin = """
Feature: Math API

Scenario: Square of a number
  When POST /number
  And request-body (number)
  Then status 200
  And response-body (number)
""".trim()

        val request = HttpRequest(method = "POST", path = "/number", body = NumberValue(10))
        val response = HttpResponse(status = 200, body = "100")

        ContractFake(gherkin, listOf(MockScenario(request, response))).use { fake ->
            val postResponse = RestTemplate().postForEntity<String>(fake.endPoint + "/number", "10")
            assertThat(postResponse.body).isEqualTo("100")
        }
    }

    @Test
    fun `it should accept (datetime) as a value in the request, and match datetime values against that type`() {
        val gherkin = """Feature: Calendar
Scenario: Accept a date
When POST /date
And request-body (datetime)
Then status 200
And response-body (string)
        """.trim()

        val request = HttpRequest("POST", "/date", emptyMap(), StringValue("(datetime)"))
        val mock = MockScenario(request, HttpResponse(200, "done"))

        ContractFake(gherkin, listOf(mock)).use { fake ->
            val postResponse = RestTemplate().postForEntity<String>(fake.endPoint + "/date", "2020-04-12T00:00:00")
            assertThat(postResponse.statusCode.value()).isEqualTo(200)
            assertThat(postResponse.body).isEqualTo("done")
        }
    }

    @Test
    fun `it should accept (datetime) as a mock value in a json request, and match datetime values against that type`() {
        val gherkin = """Feature: Calendar
Scenario: Accept a date
When POST /date
And request-body
 | date | (datetime) |
Then status 200
And response-body (string)
        """.trim()

        val request = HttpRequest("POST", "/date", emptyMap(), parsedValue("""{"date": "(datetime)"}"""))
        val mock = MockScenario(request, HttpResponse(200, "done"))

        ContractFake(gherkin, listOf(mock)).use { fake ->
            val postResponse = RestTemplate().postForEntity<String>(fake.endPoint + "/date", """{"date": "2020-04-12T00:00:00"}""")
//            val postResponse = RestTemplate().postForEntity<String>(fake.endPoint + "/date", """2020-04-12T00:00:00""")
            assertThat(postResponse.statusCode.value()).isEqualTo(200)
            assertThat(postResponse.body).isEqualTo("done")
        }
    }

    @Test
    fun `it should not accept an incorrectly formatted value`() {
        val gherkin = """Feature: Calendar
Scenario: Accept a date
When POST /date
And request-body
 | date | (datetime) |
Then status 200
And response-body (string)
        """.trim()

        val request = HttpRequest("POST", "/date", emptyMap(), parsedValue("""{"date": "(datetime)"}"""))
        val mock = MockScenario(request, HttpResponse(200, "done"))

        try {
            ContractFake(gherkin, listOf(mock)).use { fake ->
                val postResponse = RestTemplate().postForEntity<String>(fake.endPoint + "/date", """2020-04-12T00:00:00""")
                assertThat(postResponse.statusCode.value()).isEqualTo(200)
                assertThat(postResponse.body).isEqualTo("done")
            }
        } catch(e: HttpClientErrorException) {
            return
        }

        fail("Should have thrown an exception")
    }
}

package run.qontract.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import run.qontract.core.pattern.*
import run.qontract.core.value.StringValue

internal class HttpResponsePatternTest {
    @Test
    fun `it should result in 2 tests` () {
        val list = HttpResponsePattern(status = 200, headersPattern = HttpHeadersPattern(mapOf("X-Optional?" to StringPattern))).newBasedOn(Row(), Resolver())

        assertThat(list).hasSize(2)

        val flags = list.map {
            when {
                it.headersPattern.pattern.containsKey("X-Optional") -> "with"
                else -> "without"
            }
        }

        flagsContain(flags, listOf("with", "without"))
    }

    @Test
    fun `it should encompass itself`() {
        val httpResponsePattern = HttpResponsePattern(status = 200, headersPattern = HttpHeadersPattern(mapOf("X-Optional?" to StringPattern)))
        httpResponsePattern shouldEncompass httpResponsePattern
    }

    @Test
    fun `it should encompass another smaller response pattern`() {
        val bigger = HttpResponsePattern(status = 200, headersPattern = HttpHeadersPattern(mapOf("X-Required" to StringPattern)), body = toTabularPattern(mapOf("data" to AnyPattern(listOf(StringPattern, NullPattern)))))
        val smaller = HttpResponsePattern(status = 200, headersPattern = HttpHeadersPattern(mapOf("X-Required" to StringPattern, "X-Extra" to StringPattern)), body = toTabularPattern(mapOf("data" to StringPattern)))
        bigger shouldEncompass smaller
    }

    @Test
    fun `it should not encompass another response pattern with an extra key in the response payload`() {
        val smaller = HttpResponsePattern(status = 200, body = toTabularPattern(mapOf("data" to StringPattern)))
        val bigger = HttpResponsePattern(status = 200, body = toTabularPattern(mapOf("data" to StringPattern, "unexpected" to StringPattern)))
        smaller shouldNotEncompass bigger
    }

    @Test
    fun `when validating a response string against a response type number, should return an error`() {
        val response = HttpResponse(200, emptyMap(), StringValue("not a number"))
        val pattern = HttpResponsePattern(status = 200, body = NumberPattern)

        assertThat(pattern.matches(response, Resolver())).isInstanceOf(Result.Failure::class.java)
    }
}

private infix fun HttpResponsePattern.shouldNotEncompass(second: HttpResponsePattern) {
    val result = this.encompasses(second, Resolver(), Resolver())
    assertThat(result).`as`("Second response pattern should not have been backward compatible with the first").isInstanceOf(Result.Failure::class.java)
}

private infix fun HttpResponsePattern.shouldEncompass(second: HttpResponsePattern) {
    val result = this.encompasses(second, Resolver(), Resolver())
    assertThat(result).`as`(resultReport(result)).isInstanceOf(Result.Success::class.java)
}

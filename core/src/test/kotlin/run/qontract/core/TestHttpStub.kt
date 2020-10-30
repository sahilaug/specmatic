package run.qontract.core

import org.assertj.core.api.Assertions.assertThat
import run.qontract.core.utilities.exceptionCauseMessage
import run.qontract.testStub

class TestHttpStub(private val stubRequest: HttpRequest, private val stubResponse: HttpResponse) {
    fun shouldWorkWith(contractGherkin: String) {
        val response = testStub(contractGherkin, stubRequest, stubResponse)
        assertThat(response).`as`("Expected response:\n${stubResponse.toLogString()}\n\nActual response:\n${response.toLogString()}").isEqualTo(stubResponse)
    }

    fun breaksWith(contractGherkin: String) =
        try {
            val response = testStub(contractGherkin, stubRequest, stubResponse)
            if(response.status != 400) {
                println("Expected status 400, instead got this response:\n${response.toLogString()}")
            }

            response.status == 400
        } catch(e: Throwable) {
            println(exceptionCauseMessage(e))
            true
        }
}

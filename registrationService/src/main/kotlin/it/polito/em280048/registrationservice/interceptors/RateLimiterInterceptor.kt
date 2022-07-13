package it.polito.em280048.registrationservice.interceptors

import io.github.bucket4j.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter
import java.time.Duration
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class RateLimiterInterceptor: HandlerInterceptorAdapter() {

    private val tokenBucket:Bucket= Bucket4j.builder()
        .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofSeconds(1))))
        .build()
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        val probe: ConsumptionProbe = tokenBucket.tryConsumeAndReturnRemaining(1)
        return if (probe.isConsumed) {
            response.addHeader("X-Rate-Limit-Remaining", probe.remainingTokens.toString())
            true
        } else {
            println("You have exhausted your API Request Quota")
            val waitForRefill = probe.nanosToWaitForRefill / 1000000000
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", waitForRefill.toString())
            response.sendError(
                HttpStatus.TOO_MANY_REQUESTS.value(),
                "You have exhausted your API Request Quota"
            )
            false
        }


    }

}

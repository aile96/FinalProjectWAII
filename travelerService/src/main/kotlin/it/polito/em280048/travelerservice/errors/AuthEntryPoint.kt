package it.polito.em280048.travelerservice.errors

import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class AuthEntryPointJwt : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest, response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        logger.error("Unauthorized error: {}", authException.message)
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized")
    }

    companion object {
        private val logger = LoggerFactory.getLogger(AuthEntryPointJwt::class.java)
    }
}
package it.polito.s280048.ticketcatalogue.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@Component
class JWTAuthenticationFilter @Autowired constructor(private val tokenUtil: JwtUtils) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = request.getHeader("Authorization") ?: throw Exception("no jwt available")
            tokenUtil.validateJwt(token)
            val (userId, role) = tokenUtil.getDetailsJwt(token)
            if (SecurityContextHolder.getContext().authentication == null) {
                val authentication: Authentication = UsernamePasswordAuthenticationToken(
                    userId, null,
                    AuthorityUtils.createAuthorityList(role)
                )
                SecurityContextHolder.getContext().authentication = authentication
                request.setAttribute("userId", userId)
                request.setAttribute("roles", role)
            }
        } catch (e: Exception) {
            logger.error(e.message)
        }
        filterChain.doFilter(request, response)
    }
}

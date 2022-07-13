package it.polito.em280048.registrationservice.security

import io.jsonwebtoken.*
import it.polito.em280048.registrationservice.entities.User
import it.polito.em280048.registrationservice.errors.ExpiredJwtException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.util.*

@Component
class JwtUtils {

    @Value("\${registrationService.app.jwtSecret}")
    private lateinit var jwtSecret: String

    @Value("\${registrationService.app.expirationJwt}")
    private lateinit var expiration: String


    fun getDetailsJwt(authToken: String): Pair<String,String> {
        try {
            val userId = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken).body.subject
            val roles = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken).body["roles"].toString()
            return Pair(userId,roles)
        } catch (e: Exception) {
            throw Exception("invalid jwt")
        }
    }

    fun validateJwt(authToken: String): Boolean {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken)
            return true
        } catch (e: SignatureException) {
            throw Exception("Invalid JWT signature")
        } catch (e: MalformedJwtException) {
            throw Exception("Invalid JWT token")
        } catch (e: ExpiredJwtException) {
            throw Exception("JWT token is expired")
        } catch (e: UnsupportedJwtException) {
            throw Exception("JWT token is unsupported")
        } catch (e: IllegalArgumentException) {
            throw Exception("JWT claims string is empty")
        }
    }

    fun createToken(user: User): String {
        return Jwts.builder()
            .setHeaderParam("alg", "HS256")
            .setHeaderParam("type", "JWS")
            .setSubject(user.id.toString())
            .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
            .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(expiration.toLong()).toInstant()))
            .claim("roles", user.role)
            .signWith(SignatureAlgorithm.HS256, jwtSecret).compact()
    }
}
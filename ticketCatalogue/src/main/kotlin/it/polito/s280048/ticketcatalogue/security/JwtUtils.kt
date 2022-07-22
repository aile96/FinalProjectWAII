package it.polito.s280048.ticketcatalogue.security

import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.ZonedDateTime
import java.util.*

@Component
class JwtUtils {

    @Value("\${ticketCatalogue.app.jwtSecretInternal}")
    private lateinit var jwtSecretInternal: String

    fun getDetailsJwt(authToken: String): Pair<String,String> {
        try {
            val userId = Jwts.parser().setSigningKey(jwtSecretInternal).parseClaimsJws(authToken).body.subject
            val roles = Jwts.parser().setSigningKey(jwtSecretInternal).parseClaimsJws(authToken).body["roles"].toString()
            return Pair(userId,roles)
        } catch (e: Exception) {
            throw Exception("invalid jwt")
        }
    }

    fun validateJwt(authToken: String): Boolean {
        try {
            Jwts.parser().setSigningKey(jwtSecretInternal.toByteArray())
                .parseClaimsJws(authToken)
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
        } catch (e: Exception) {
            throw Exception("Unknown reason")
        }
    }

    fun createToken(sub: String): String {
        return Jwts.builder()
            .setHeaderParam("alg", "HS256")
            .setHeaderParam("type", "JWS")
            .setSubject(sub)
            .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
            .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(5).toInstant()))
            .claim("roles", "ADMIN")
            .signWith(SignatureAlgorithm.HS256, jwtSecretInternal.toByteArray()).compact()
    }
}
package it.polito.em280048.travelerservice.security

import io.jsonwebtoken.*
import it.polito.em280048.travelerservice.errors.ExceptionLong
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class JwtUtils {

    @Value("\${travelerService.app.jwtSecretInternal}")
    private lateinit var jwtSecretInternal: String
    @Value("\${travelerService.app.jwtSecretExternal}")
    lateinit var jwtSecretExternal: String

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
            Jwts.parser().setSigningKey(jwtSecretInternal).parseClaimsJws(authToken)
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

    fun createToken(sub: String, iat: LocalDateTime, exp: LocalDateTime, zid: String, validfrom: String, type: String): String {
        return Jwts.builder()
            .setHeaderParam("alg", "HS256")
            .setHeaderParam("type", "JWS")
            .setSubject(sub)
            .claim("iat", iat.toString())
            .claim("exp", exp.toString())
            .claim("zid", zid)
            .claim("validfrom", validfrom)
            .claim("type", type)
            .signWith(SignatureAlgorithm.HS256, jwtSecretExternal).compact()
    }
}
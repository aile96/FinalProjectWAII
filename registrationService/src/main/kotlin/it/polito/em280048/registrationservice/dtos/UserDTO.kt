package it.polito.em280048.registrationservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import it.polito.em280048.registrationservice.entities.User
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class UserDTO(
    @field:Email
    @JsonProperty("email")
    val email: String,
    @field:NotBlank
    @JsonProperty("nickname")
    val nickname: String,
    @field:Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$")
    @JsonProperty("password")
    val password: String
)

fun User.toDTO(): UserDTO {
    return UserDTO(email, username, password)
}
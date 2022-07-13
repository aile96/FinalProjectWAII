package it.polito.em280048.registrationservice.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.Email

data class UserLoginDTO(
    @field:Email
    @JsonProperty("email")
    val email: String,

    @JsonProperty("password")
    val password: String
)

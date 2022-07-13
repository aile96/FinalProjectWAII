package it.polito.em280048.registrationservice.dtos

import it.polito.em280048.registrationservice.entities.User

data class ValidateCodeResponseDTO(val userId: Long, val nickname: String, val email: String)

fun User.toValidateCodeResponseDTO(): ValidateCodeResponseDTO {
    return ValidateCodeResponseDTO(id, username, email)
}
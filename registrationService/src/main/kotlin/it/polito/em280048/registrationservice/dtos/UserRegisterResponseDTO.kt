package it.polito.em280048.registrationservice.dtos

import it.polito.em280048.registrationservice.entities.User
import java.util.*

data class UserRegisterResponseDTO(val provisional_id: UUID, val email: String)

fun User.toRegisterResponseDTO(): UserRegisterResponseDTO {
    return UserRegisterResponseDTO(activation.id, email)
}
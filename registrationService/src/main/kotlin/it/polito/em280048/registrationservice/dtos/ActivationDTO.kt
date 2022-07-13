package it.polito.em280048.registrationservice.dtos

import it.polito.em280048.registrationservice.entities.Activation
import java.util.*

data class ActivationDTO(val provisional_id: UUID, val activation_code: String)

fun Activation.toDTO(): ActivationDTO {
    return ActivationDTO(id, code)
}
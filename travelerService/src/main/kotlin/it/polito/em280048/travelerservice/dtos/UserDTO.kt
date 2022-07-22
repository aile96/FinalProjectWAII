package it.polito.em280048.travelerservice.dtos

import it.polito.em280048.travelerservice.entities.User
import java.time.format.DateTimeFormatter

data class UserDTO(
    val id: Long?,
    val name: String?,
    val address: String?,
    val date_of_birth: String?,
    val telephone_number: String?
)

fun User.toDTO(formatter: String): UserDTO {
    return UserDTO(idUsr, name, address, dateOfBirth?.format(DateTimeFormatter.ofPattern(formatter)), phoneNumber)
}
package it.polito.s280048.ticketcatalogue.dtos

import java.time.LocalDate

data class UserDetailsDTOIncoming(
    val name: String?,
    val address: String?,
    val date_of_birth: LocalDate?,
    val telephone_number: String?
)

data class MiddleIncoming(
    val profile: UserDetailsDTOIncoming
)

data class Incoming(
    val data: MiddleIncoming
)

data class UserDetailsDTO(
    val name: String,
    var date_of_birth: LocalDate
)
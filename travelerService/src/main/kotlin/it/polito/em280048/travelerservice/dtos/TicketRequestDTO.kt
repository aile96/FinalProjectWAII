package it.polito.em280048.travelerservice.dtos

data class TicketRequestDTO(
    var cmd: String,
    var quantity: Int,
    var zones: String,
    val validfrom: String,
    val exp: String,
    val type: Int
)

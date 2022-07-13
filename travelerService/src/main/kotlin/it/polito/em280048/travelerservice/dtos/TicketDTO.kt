package it.polito.em280048.travelerservice.dtos

import it.polito.em280048.travelerservice.entities.Ticket
import java.time.LocalDateTime

data class TicketDTO(
    var sub: Long,
    var iat: LocalDateTime,
    var exp: LocalDateTime,
    var zid: String,
    var jws: String,
    var validfrom: LocalDateTime,
    var type: Int
)

fun Ticket.toDTO(zid: Long): TicketDTO {
    return TicketDTO(
        id,
        issuedAt,
        expireAt,
        zid.toString(),
        jwtUtils.createToken(id.toString(),issuedAt,expireAt,zid.toString(), validfrom.toString(), type.toString()),
        validfrom,
        type
    )
}
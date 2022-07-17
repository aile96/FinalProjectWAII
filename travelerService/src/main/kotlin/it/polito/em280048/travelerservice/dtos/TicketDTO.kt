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

data class TicketDTOForString(
    var sub: Long,
    var iat: String,
    var exp: String,
    var zid: String,
    var jws: String,
    var validfrom: String,
    var type: Int
)

fun TicketDTO.toForString(): TicketDTOForString {
    return TicketDTOForString(
        sub,
        iat.toString(),
        exp.toString(),
        zid,
        jws,
        validfrom.toString(),
        type
    )
}

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
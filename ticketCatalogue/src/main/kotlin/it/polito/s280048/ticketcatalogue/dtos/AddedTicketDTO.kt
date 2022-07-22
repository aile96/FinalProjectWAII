package it.polito.s280048.ticketcatalogue.dtos
import it.polito.s280048.ticketcatalogue.entities.Type

data class AddedTicketDTO(val price: Double, val type: Type, val exp: Int?, val zid: Int)


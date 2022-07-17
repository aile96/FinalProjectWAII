package it.polito.em280048.travelerservice.repositories

import it.polito.em280048.travelerservice.entities.Ticket
import org.springframework.data.repository.CrudRepository

interface TicketRepository : CrudRepository<Ticket, Long> {
    fun findAllByUserDetailsId(userDetails_id: Long): MutableList<Ticket>
    //fun findAllByTicketsZoneHash(ticketsZone_hash: String): MutableList<Ticket>
    //fun findTop1ByOrderByTicketsZoneIdentifierDesc(): Ticket?
}
package it.polito.s280048.ticketcatalogue.repositories

import it.polito.s280048.ticketcatalogue.entities.Ticket
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketCatalogueRepository: ReactiveCrudRepository<Ticket, Long> {
}
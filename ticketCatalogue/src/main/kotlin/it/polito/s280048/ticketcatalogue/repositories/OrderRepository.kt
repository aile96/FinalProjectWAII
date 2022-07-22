package it.polito.s280048.ticketcatalogue.repositories

import it.polito.s280048.ticketcatalogue.entities.Order
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderRepository: ReactiveCrudRepository<Order, Long> {
}
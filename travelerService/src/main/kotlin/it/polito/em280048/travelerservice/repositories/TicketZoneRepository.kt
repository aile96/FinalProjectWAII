package it.polito.em280048.travelerservice.repositories

import it.polito.em280048.travelerservice.entities.TicketZone
import it.polito.em280048.travelerservice.entities.TicketZoneKey
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface TicketZoneRepository: CrudRepository<TicketZone, TicketZoneKey> {
    fun findAllByHash(hash: String): MutableList<TicketZone>
    //fun findFirstOrderByHashDesc(): MutableList<TicketZone>
    @Query("select max(tz.identifier) from TicketZone tz")
    fun findMax(): Long?
}
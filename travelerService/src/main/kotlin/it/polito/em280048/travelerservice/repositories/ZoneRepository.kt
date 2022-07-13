package it.polito.em280048.travelerservice.repositories

import it.polito.em280048.travelerservice.entities.Zone
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ZoneRepository : CrudRepository<Zone, Long> {
    fun findByZoneName(name: String): Zone?
}
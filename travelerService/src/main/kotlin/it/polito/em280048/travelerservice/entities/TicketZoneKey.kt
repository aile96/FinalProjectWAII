package it.polito.em280048.travelerservice.entities

import java.io.Serializable
import javax.persistence.*

@Embeddable
class TicketZoneKey : Serializable {

    @Column(name = "ticket_id")
    var ticketId: Long = 1L

    @Column(name = "zone_id")
    var zoneId: Long = 1L

}
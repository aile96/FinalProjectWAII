package it.polito.em280048.travelerservice.entities

import java.math.BigInteger
import java.security.MessageDigest
import javax.persistence.*

@Entity
class TicketZone {
    @EmbeddedId
    var id: TicketZoneKey = TicketZoneKey()

    @ManyToOne
    @MapsId("ticketId")
    @JoinColumn(name = "ticket_id")
    var ticket: Ticket? = null

    @ManyToOne
    @MapsId("zoneId")
    @JoinColumn(name = "zone_id")
    var zone: Zone? = null

    var hash: String = ""

    var identifier: Long = 1L

    companion object {
        fun computeHash(zones: MutableList<Zone>): String {
            val md = MessageDigest.getInstance("MD5")
            var input = "";
            zones.forEach { input += it.zoneName };
            return BigInteger(1, md.digest(input.toByteArray())).toString(16).padStart(32, '0').toString()
        }
    }
}
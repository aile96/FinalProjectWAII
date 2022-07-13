package it.polito.em280048.travelerservice.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import javax.persistence.*

@Entity
class Zone: Comparable<Zone> {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0L

    @Column(unique = true)
    lateinit var zoneName: String

    @JsonIgnore
    @OneToMany(mappedBy = "zone")
    val ticketsZone: MutableList<TicketZone> = mutableListOf()

    override fun compareTo(other: Zone): Int {
        return id.compareTo(other.id)
    }
}
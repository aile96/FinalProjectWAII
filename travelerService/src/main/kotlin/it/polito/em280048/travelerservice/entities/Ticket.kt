package it.polito.em280048.travelerservice.entities

import it.polito.em280048.travelerservice.security.JwtUtils
import java.time.LocalDateTime
import javax.persistence.*
import kotlin.jvm.Transient


@Entity
class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0L

    var issuedAt: LocalDateTime = LocalDateTime.now()

    var expireAt: LocalDateTime = LocalDateTime.now().plusHours(1)

    @OneToMany(mappedBy = "ticket")
    var ticketsZone: MutableList<TicketZone> = mutableListOf()

    @ManyToOne
    var userDetails: User? = null

    var validfrom: LocalDateTime = LocalDateTime.now()

    var type: Int = 0

    @Transient
    lateinit var jwtUtils: JwtUtils
}
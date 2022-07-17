package it.polito.em280048.travelerservice.entities

import java.time.LocalDate
import javax.persistence.*

@Entity
@Table(name = "user_details")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0L,
    var name: String? = null,
    var address: String? = null,
    var dateOfBirth: LocalDate? = null,
    var phoneNumber: String? = null
) {

    @OneToMany(mappedBy = "userDetails")
    val ticket = mutableListOf<Ticket>()
    fun addTicket(t: Ticket) {
        t.userDetails = this
        ticket.add(t)
    }
}
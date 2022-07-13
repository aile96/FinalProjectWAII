package it.polito.em280048.travelerservice.entities

import java.time.LocalDate
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "user_details")
class User(
    @Id var id: Long,
    var name: String?,
    var address: String?,
    var dateOfBirth: LocalDate?,
    var phoneNumber: String?
) {

    @OneToMany(mappedBy = "userDetails")
    val ticket = mutableListOf<Ticket>()
    fun addTicket(t: Ticket) {
        t.userDetails = this;
        ticket.add(t)
    }
}
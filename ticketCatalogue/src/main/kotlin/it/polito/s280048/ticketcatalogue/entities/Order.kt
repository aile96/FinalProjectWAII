package it.polito.s280048.ticketcatalogue.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("orders")
class Order {
    @Id
    var id: Long = 0L
    @Column("username")
    var user: Long = 0L
    @Column("ticket_id")
    var ticketId: Long= 0L
    @Column("n_ticket")
    var nTicket: Int = 0

    var status: Status = Status.PENDING

    /*override fun toString(): String {
        return (id.toString()+user+ticketId.toString()+nTicket.toString()+status.toString())
    }*/

}

enum class Status {
    PENDING, REJECTED, COMPLETED
}
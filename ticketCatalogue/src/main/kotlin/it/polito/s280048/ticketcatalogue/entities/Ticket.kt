package it.polito.s280048.ticketcatalogue.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("ticket")
class Ticket {
    @Id
    var id: Long = 0L

    var price: Double = 0.0

    var type: Type = Type.ORDINAL

    //Hours for the expiration of the seasonal tickets (null if ordinal)
    var exp: Int? = null

    //List of zone names separated by ;
    var zid: String = ""
}

enum class Type {
    ORDINAL, SEASONAL
}
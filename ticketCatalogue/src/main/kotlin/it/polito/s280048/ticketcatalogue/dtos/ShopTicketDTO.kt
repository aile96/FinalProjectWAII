package it.polito.s280048.ticketcatalogue.dtos


data class ShopTicketDTO(
    val nTicket: Int,
    val infoPayment: PaymentInfoDTO
)
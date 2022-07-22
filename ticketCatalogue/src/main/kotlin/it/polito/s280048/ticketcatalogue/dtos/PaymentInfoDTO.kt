package it.polito.s280048.ticketcatalogue.dtos

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class PaymentInfoDTO(
    val creditCardNumber: Long,
    val exp: LocalDate,
    val cvv: Int,
    val holder: String
)

data class PaymentInfoDTOQueue(
    @JsonProperty("creditCardNumber")
    val creditCardNumber: Long,
    @JsonProperty("exp")
    val exp: String,
    @JsonProperty("cvv")
    val cvv: Int,
    @JsonProperty("holder")
    val holder: String,
    @JsonProperty("totalCost")
    val totalCost: Double,
    @JsonProperty("userId")
    val userId: String,
    @JsonProperty("done")
    val done: Int,
    @JsonProperty("orderId")
    val orderId: Long
)

public fun paymentInfoDTOToString (p: PaymentInfoDTO, totalCost: Double, userId: String, done: Int, orderId: Long): PaymentInfoDTOQueue {
    return PaymentInfoDTOQueue(p.creditCardNumber,p.exp.toString(),p.cvv,p.holder,totalCost,userId,done,orderId)
}
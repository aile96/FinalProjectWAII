package it.polito.s280048.ticketcatalogue.config.consumer

import com.netflix.discovery.EurekaClient
import it.polito.s280048.ticketcatalogue.dtos.PaymentInfoDTOQueue
import it.polito.s280048.ticketcatalogue.entities.Order
import it.polito.s280048.ticketcatalogue.entities.Status
import it.polito.s280048.ticketcatalogue.entities.Ticket
import it.polito.s280048.ticketcatalogue.entities.Type
import it.polito.s280048.ticketcatalogue.repositories.OrderRepository
import it.polito.s280048.ticketcatalogue.repositories.TicketCatalogueRepository
import it.polito.s280048.ticketcatalogue.security.JwtUtils
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.body
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.time.LocalDateTime


@Component
class Consumer(private val discoveryClient: EurekaClient, val jwtUtils: JwtUtils) {

    @Autowired
    lateinit var db: OrderRepository

    @Autowired
    lateinit var dbTicket: TicketCatalogueRepository

    @KafkaListener(topics = ["\${kafka.topics.consume}"], groupId = "ppr")
    fun listenGroupFoo(consumerRecord: ConsumerRecord<Any, Any>, ack: Acknowledgment) {
        val order = db.findById((consumerRecord.value() as PaymentInfoDTOQueue).orderId).block()
        if ((consumerRecord.value() as PaymentInfoDTOQueue).done != 1) {
            order!!.status = Status.REJECTED
        } else {
            order!!.status = Status.COMPLETED
        }
        val tick = dbTicket.findById(order.ticketId).block()!!
        try {
            if (insertTicket(order, tick).block()!!.substring(0,8) == "{\"errors")
                throw Exception()
        } catch (e: Exception) {
            ack.acknowledge()
            return
        }
        this.db.save(order).block()!!
        ack.acknowledge()
    }

    fun insertTicket(order: Order, ticket: Ticket): Mono<String> {
        val exp = LocalDateTime.now().plusHours(ticket.exp?.toLong() ?: 1)
        val type = if(ticket.type==Type.ORDINAL) 0 else 1
        val body = "{\"query\":\"mutation { insertTicket(ticket:{\\n cmd: \\\"buy_tickets\\\"\\n quantity: ${order.nTicket}\\n zones: \\\"${ticket.zid}\\\"\\n type: ${type}\\n validfrom: \\\"${LocalDateTime.now()}\\\"\\n exp: \\\"${exp}\\\"\\n}) {\\n sub\\n iat\\n exp\\n zid\\n validfrom\\n type\\n jws\\n}}\",\"operationName\":null}"
        val app = discoveryClient.getApplication("travelerService").instances
        return WebClient.create(app[(0 until app.size).random()].homePageUrl).post().uri("/graphql").header(
            HttpHeaders.AUTHORIZATION, jwtUtils.createToken(order.user.toString())
        )
            .body(Mono.just(body)).header(HttpHeaders.CONTENT_TYPE, "application/json")
            .header(HttpHeaders.ACCEPT, "*/*")
            .retrieve().bodyToMono()
    }
}
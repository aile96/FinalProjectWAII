package it.polito.s280048.ticketcatalogue.controllers

import com.netflix.discovery.EurekaClient
import it.polito.s280048.ticketcatalogue.entities.Order
import it.polito.s280048.ticketcatalogue.entities.Ticket
import it.polito.s280048.ticketcatalogue.services.TicketCatalogueService
import it.polito.s280048.ticketcatalogue.dtos.AddedTicketDTO
import it.polito.s280048.ticketcatalogue.dtos.PaymentInfoDTO
import it.polito.s280048.ticketcatalogue.dtos.ShopTicketDTO
import it.polito.s280048.ticketcatalogue.dtos.paymentInfoDTOToString
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.LocalDate

@RestController
class TicketCatalogueController(val ticketCatalogueService: TicketCatalogueService,
                                @Value("\${kafka.topics.produce}") val topic: String,
                                @Autowired private val kafkaTemplate: KafkaTemplate<String, Any>,
) {

    @GetMapping("/ticket", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    fun getAllTickets(): Flux<Ticket> {
        return ticketCatalogueService.getAllTickets()
    }

    @GetMapping("/admin/orders")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun getAllOrders():Flux<Order>{
        return ticketCatalogueService.getAllOrders()
    }

    @PostMapping("/shop/{ticketID}")
    @PreAuthorize("hasAuthority('USER')")
    suspend fun buyTicket(@PathVariable ticketID: Long, @RequestBody dto: ShopTicketDTO, @RequestHeader head: Map<String, String>): ResponseEntity<Any> {
        return ResponseEntity.ok(ticketCatalogueService.buyTicket(ticketID, dto, head, getCurrentUserDetails()))
    }

    @PostMapping("/admin/tickets")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun createNewTicket(@RequestBody ticketDTO: AddedTicketDTO):ResponseEntity<Any>{
        return ResponseEntity.ok(ticketCatalogueService.addTicket(ticketDTO))

    }

    @GetMapping("/orders", produces = [MediaType.APPLICATION_NDJSON_VALUE])
    @PreAuthorize("hasAuthority('USER')")
    suspend fun getOrders():Flux<Order>{
        return ticketCatalogueService.findOrders()
    }

    @GetMapping("/orders/{orderId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getOrderById(@PathVariable orderId:Long):Mono<ResponseEntity<Order>>{
        val order:Mono<Order> =ticketCatalogueService.findOrderById(orderId)
        return order.map { t: Order ->
            ResponseEntity.ok(t)
        }.defaultIfEmpty(ResponseEntity.notFound().build())

    }

    @GetMapping("/ticket/{ticketId}", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getTicketById(@PathVariable ticketId: Long): Mono<ResponseEntity<Ticket>> {
        val ticket: Mono<Ticket> = ticketCatalogueService.findById(ticketId)
        return ticket.map { t: Ticket ->
            ResponseEntity.ok(t)
        }
            .defaultIfEmpty(ResponseEntity.notFound().build())
    }

    @GetMapping("/admin/orders/{userId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    suspend fun getSpecificUsersOrder(@PathVariable userId:Long):Flux<Order>{
       return ticketCatalogueService.getOrderFromUserId(userId)
    }

    @GetMapping("test/kafka")
    fun testkafka() :ResponseEntity<Any>{
        val paymentDTO = PaymentInfoDTO(123456, LocalDate.now(),234,"setare")
        try {
            val message = MessageBuilder
                .withPayload(paymentInfoDTOToString(paymentDTO,10.0,"111",12,1L))
                .setHeader(KafkaHeaders.TOPIC, topic)
                //.setHeader("X-Custom-Header", "Custom header here")
                .build()
            kafkaTemplate.send(message)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
        }
        return ResponseEntity.ok().build()
    }


    fun getCurrentUserDetails(): String {
        return (SecurityContextHolder.getContext().authentication.principal as String?) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "user must exists")
    }
}
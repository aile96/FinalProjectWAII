package it.polito.s280048.ticketcatalogue.services

import com.netflix.discovery.EurekaClient
import it.polito.s280048.ticketcatalogue.dtos.AddedTicketDTO
import it.polito.s280048.ticketcatalogue.dtos.Incoming
import it.polito.s280048.ticketcatalogue.dtos.ShopTicketDTO
import it.polito.s280048.ticketcatalogue.dtos.paymentInfoDTOToString
import it.polito.s280048.ticketcatalogue.entities.Order
import it.polito.s280048.ticketcatalogue.entities.Status
import it.polito.s280048.ticketcatalogue.entities.Ticket
import it.polito.s280048.ticketcatalogue.entities.Type
import it.polito.s280048.ticketcatalogue.repositories.OrderRepository
import it.polito.s280048.ticketcatalogue.repositories.TicketCatalogueRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.body
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toFlux
import java.time.LocalDate
import java.util.*


@Service
@Transactional
class TicketCatalogueService(
    val ticketCatalogueRepository: TicketCatalogueRepository,
    val orderRepository: OrderRepository,
    private val discoveryClient: EurekaClient,
    @Value("\${kafka.topics.produce}") val topic: String,
    @Autowired private val kafkaTemplate: KafkaTemplate<String, Any>) {

    @RequestMapping
    fun getAllTickets(): Flux<Ticket> {
        return ticketCatalogueRepository.findAll()
    }

    fun getAllOrders():Flux<Order>{
        return orderRepository.findAll()
    }

    fun findOrders():Flux<Order>{
        return orderRepository.findAll().publishOn(Schedulers.boundedElastic()).filter{
            it.user == getCurrentUserDetails().toLong()
        }.map{it}
            .toFlux()
    }

    suspend fun buyTicket(ticketID: Long, dto: ShopTicketDTO, head: Map<String, String>, userId: String): Mono<Long> {
        val user = takeUser(head, userId)
        val ticket = findById(ticketID)
        var order = ticket.zipWith(user).map {
            if(it.t1.type == Type.SEASONAL) {
                if((it.t2.data.profile.date_of_birth!!.plusYears(27).compareTo(LocalDate.now()))!=-1) {
                    throw ResponseStatusException(HttpStatus.FORBIDDEN,"service not available for your age")
                }
            }
            val order = Order()
            order.ticketId = ticketID
            order.user = userId.toLong()
            order.nTicket = dto.nTicket
            order.status = Status.PENDING
            order
        }
        var price: Double
        val orderDeMono = withContext(Dispatchers.IO) {
            price = ticket.block()?.price ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
            order = orderRepository.save(
                order.block() ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
            )
            order.block() ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
        }
        try {
            val message = MessageBuilder
                .withPayload(paymentInfoDTOToString(dto.infoPayment,(dto.nTicket.toDouble() * price),orderDeMono.user.toString(),0,orderDeMono.id))
                .setHeader(KafkaHeaders.TOPIC, topic)
                //.setHeader("X-Custom-Header", "Custom header here")
                .build()
            kafkaTemplate.send(message)
        } catch (e: Exception) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
        }

        return order.map{it.id}
    }

    fun addTicket(ticketDTO: AddedTicketDTO): Mono<Long>{
        val ticket = Ticket()
        ticket.price = ticketDTO.price
        ticket.type = ticketDTO.type
        return ticketCatalogueRepository.save(ticket).map { it.id }
    }

    fun findOrderById(orderId:Long):Mono<Order>{
        return orderRepository.findById(orderId)
    }

    fun findById(ticketId: Long): Mono<Ticket> {
        return ticketCatalogueRepository.findById(ticketId)
    }

    fun getOrderFromUserId(userId: Long):Flux<Order>{
        return orderRepository.findAll().filter { it.user == userId }
    }

    suspend fun takeUser(head: Map<String, String>, userId: String): Mono<Incoming> {
        val body = withContext(Dispatchers.IO) { createBody(userId) }
        val app = discoveryClient.getApplication("travelerService").instances
        return WebClient.create(app[(0 until app.size).random()].homePageUrl).post().uri("/graphql").header(HttpHeaders.AUTHORIZATION, head[HttpHeaders.AUTHORIZATION.lowercase(Locale.getDefault())])
            .body(Mono.just(body)).header(HttpHeaders.CONTENT_TYPE, "application/json").header(HttpHeaders.ACCEPT, "*/*")
            .retrieve().bodyToMono()
    }

    fun createBody(id: String): String {
        return "{\"query\":\"query {\\n\\tprofile(id: \\\"$id\\\") {\\n\\t\\tname\\n\\t\\taddress\\n\\t\\tdateOfBirth\\n\\t\\tphoneNumber\\n\\t}\\n}\",\"operationName\":null}"
    }

    fun getCurrentUserDetails(): String {
        return (SecurityContextHolder.getContext().authentication.principal as String?) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "user must exists")
    }

}
package it.polito.em280048.travelerservice.controllers

import com.google.gson.Gson
import io.github.g0dkar.qrcode.QRCode
import it.polito.em280048.travelerservice.dtos.TicketRequestDTO
import it.polito.em280048.travelerservice.dtos.UserDTO
import it.polito.em280048.travelerservice.dtos.toForString
import it.polito.em280048.travelerservice.security.JwtUtils
import it.polito.em280048.travelerservice.services.TicketService
import it.polito.em280048.travelerservice.services.UserService
import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.core.env.Environment
import org.springframework.core.io.ByteArrayResource
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import java.io.ByteArrayOutputStream
import java.util.*
import javax.servlet.http.HttpServletResponse

@Controller
class GraphQLController(private val userService: UserService, private val ticketService: TicketService, val jwtUtils: JwtUtils, private val discoveryClient: DiscoveryClient, private val env:Environment) {

    @QueryMapping(name = "service/services")
    fun services(): Any {
        return discoveryClient.services
    }

    @QueryMapping(name = "/services/port")
    fun port(): Any {
        return "Service Port number: "+ env.getProperty("localhost.server.port")
    }

    @QueryMapping(name = "profile")
    fun profile(@Argument id: String?): Any {
        return userService.getProfile(id)
    }

    @QueryMapping
    fun tickets(@Argument id: String?): Any {
        return ticketService.getTicket(id)
    }

    @QueryMapping
    @PreAuthorize("hasAuthority('MACHINE')")
    fun getCode(): Any {
        return jwtUtils.jwtSecretExternal
    }

    @QueryMapping
    fun adminProfile(): Any {
        return userService.getUsernames()
    }

    @QueryMapping
    fun generateQrCode(@Argument id: String): Any {
        val imageOut = ByteArrayOutputStream()
        val ticket = ticketService.getSpecificTicket(id).toForString()
        QRCode(Gson().toJson(ticket)).render().writeImage(imageOut)
        return Base64.getEncoder().encodeToString(ByteArrayResource(imageOut.toByteArray(), MediaType.IMAGE_PNG_VALUE).byteArray)
    }

    @MutationMapping
    fun modifyProfile(@Argument profile: UserDTO, response: HttpServletResponse): Any {
        userService.updateUserProfile(profile)
        return "OK"
    }

    @MutationMapping
    fun insertTicket(@Argument ticket: TicketRequestDTO, response: HttpServletResponse): Any {
        return ticketService.generateTickets(ticket)
    }
}
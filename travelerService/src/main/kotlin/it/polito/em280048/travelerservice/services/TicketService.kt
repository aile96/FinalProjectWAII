package it.polito.em280048.travelerservice.services

import it.polito.em280048.travelerservice.dtos.*
import it.polito.em280048.travelerservice.entities.*
import it.polito.em280048.travelerservice.repositories.*
import it.polito.em280048.travelerservice.security.JwtUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component
import org.springframework.web.server.ResponseStatusException
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
@Transactional
class TicketService @Autowired constructor(
    private val ticketRepository: TicketRepository,
    private val userService: UserService,
    private val zoneRepository: ZoneRepository,
    private val jwtUtils: JwtUtils,
    private val ticketZoneRepository: TicketZoneRepository
) {
    @Value("\${travelerService.app.expirationJwt}")
    private lateinit var expiration: String

    fun getTicket(id: String?): List<TicketDTO> {
        val identifier = if(!userService.isAdmin()) {
            userService.getCurrentUserDetails().toLong()
        } else {
            id?.toLong() ?: throw Exception("Bad request")
        }
        val ticketDetails = ticketRepository.findAllByUserDetailsId(identifier)
        if(ticketDetails.isEmpty())
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        return ticketDetails.map { it.toDTO(it.ticketsZone[0].identifier) }
    }

    fun getSpecificTicket(id: String): TicketDTO {
        val ticketDetails = ticketRepository.findById(id.toLong())
        val ticket: Ticket?
        if((ticketDetails.isEmpty)||(userService.getCurrentUserDetails().toLong()!=ticketDetails.get().userDetails?.id))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        else
            ticket = ticketDetails.get()
        ticket.jwtUtils = jwtUtils
        return ticket.toDTO(ticket.ticketsZone[0].identifier)
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    fun generateTickets(ticket: TicketRequestDTO): MutableList<TicketDTO> {
        val user = userService.getCurrentUserDetails()
        val userDetails = userService.findById(user.toLong())
        val zones = mutableListOf<Zone>()
        val ticketPurchasedDTO = mutableListOf<TicketDTO>()

        //extract the zoneNames and create the Zone Objs
        for (z in ticket.zones.split(" ")) {
            val zone = zoneRepository.findByZoneName(z)
            if (zone == null)
                throw Exception("Bad request")
            else
                zones.add(zone)
        }

        val toCreate = createTicketIdentifier(zones)
        for (t in 1..ticket.quantity) {
            var plusIat: Long
            try {
                plusIat = expiration.toLong()
            } catch(e: Exception) {
                throw Exception("Bad request")
            }
            var ticketPurchased = Ticket()
            ticketPurchased.userDetails = userDetails
            ticketPurchased.issuedAt = LocalDateTime.now()
            ticketPurchased.expireAt = ticketPurchased.issuedAt.plusMinutes(plusIat)
            ticketPurchased.type = ticket.type
            ticketPurchased.validfrom = LocalDateTime.parse(ticket.validfrom)
            ticketPurchased = ticketRepository.save(ticketPurchased)
            ticketZoneRepository.saveAll(createTicketZone(zones,ticketPurchased,toCreate).toMutableList())
            ticketPurchased.jwtUtils = jwtUtils
            ticketPurchasedDTO.add(ticketPurchased.toDTO(toCreate.first))
        }
        return ticketPurchasedDTO
    }

    fun createTicketIdentifier(zones: MutableList<Zone>): Pair<Long,String> {
        val hash = TicketZone.computeHash(zones.sortedByDescending { it }.toMutableList())
        val list = ticketZoneRepository.findAllByHash(hash)
        return if (list.isEmpty()) {
            Pair((ticketZoneRepository.findMax() ?: 0) + 1, hash)
        } else { //Check if in the list there is the correct one
            val l = list.distinctBy { it.identifier }.filter { zone ->
                val l = list.filter { zone.identifier == it.identifier }
                l.map { it.zone }.containsAll(zones) && zones.containsAll(l.map { it.zone })
            }
            Pair(
                if (l.isEmpty()) { (ticketZoneRepository.findMax() ?: 0) + 1 } else { l[0].identifier }
                , hash)
        }
    }

    fun createTicketZone(zones: MutableList<Zone>, ticket: Ticket, key: Pair<Long,String>): List<TicketZone> {
        return zones.map {
            val ticketZone = TicketZone()
            ticketZone.zone = it
            ticketZone.hash = key.second
            ticketZone.identifier = key.first
            ticketZone.ticket = ticket
            ticketZone
        }
    }
}
package it.polito.em280048.travelerservice.services

import it.polito.em280048.travelerservice.dtos.UserDTO
import it.polito.em280048.travelerservice.dtos.toDTO
import it.polito.em280048.travelerservice.entities.User
import it.polito.em280048.travelerservice.repositories.UserRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Component
@Transactional
class UserService(private val userRepository: UserRepository) {

    @Value("\${travelerService.app.formattingDate}")
    lateinit var formatter: String

    fun getCurrentUserDetails(): String {
        return SecurityContextHolder.getContext().authentication.principal.toString()
    }

    fun isAdmin(): Boolean {
        val auth: Authentication = SecurityContextHolder.getContext().authentication
        return auth.authorities.stream()
            .anyMatch { r -> r.authority.equals("ADMIN") }
    }

    fun getProfile(id: String?): UserDTO {
        val userDetails = if(!isAdmin()) {
            findById(getCurrentUserDetails().toLong())
        } else {
            if (id==null) {
                throw Exception("User not found")
            }
            findById(id.toLong())
        }
        return userDetails.toDTO(formatter)
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    fun getUsernames(): MutableList<String> {
        val usernames = mutableListOf<String>()
        userRepository.findAll().forEach {
            usernames.add(it.id.toString())
        }
        return usernames
    }

    fun updateUserProfile(userDTO: UserDTO) {
        val user = getCurrentUserDetails()
        val userDetails = userRepository.findByIdUsr(user.toLong()) ?: User(idUsr = user.toLong())
        try {
            userDetails.dateOfBirth = LocalDate.parse(userDTO.date_of_birth, DateTimeFormatter.ofPattern(formatter))
        } catch (e: Exception) {
            throw Exception("Bad request")
        }
        userDetails.name = userDTO.name
        userDetails.address = userDTO.address
        userDetails.phoneNumber = userDTO.telephone_number
        userRepository.save(userDetails)
    }

    fun findById(id: Long): User {
        val userDetails = userRepository.findByIdUsr(id)
        if (userDetails==null) {
            val u = User(idUsr = id)
            return userRepository.save(u)
        }
        return userDetails
    }
}
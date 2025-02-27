package it.polito.em280048.travelerservice.repositories

import it.polito.em280048.travelerservice.entities.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {
    fun findByIdUsr(id: Long): User?
}
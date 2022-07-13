package it.polito.em280048.registrationservice.repositories

import it.polito.em280048.registrationservice.entities.Activation
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.*


interface ActivationRepository: CrudRepository<Activation,UUID> {
    fun findByUserId(userId: Long ): List<Activation>?

    @Modifying
    @Query("delete from Activation  where id = ?1")
    fun delete(id: UUID)
}
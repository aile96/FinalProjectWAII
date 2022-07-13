package it.polito.em280048.registrationservice.repositories

import it.polito.em280048.registrationservice.entities.User
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository


interface UserRepository: CrudRepository<User,Long> {
    @Modifying
    @Query(value = "DELETE \n" +
            "FROM USERS U \n" +
            "     USING ACTIVATION A \n" +
            "WHERE U.id = A.user_id AND\n" +
            "      A.deadline<=NOW() AND                 \n" +
            "      U.state=0;", nativeQuery = true)
    fun deleteDanglingUsers():Int
    fun findByEmail(email: String): User?
    //fun findByUsername(username: String): User?
    //fun saveAndFlush(user: User): User
}
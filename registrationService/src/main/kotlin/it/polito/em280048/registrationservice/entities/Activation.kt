package it.polito.em280048.registrationservice.entities

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*


@Entity
@Table(name = "activation")
class Activation {

    @Id
    var id: UUID = UUID.randomUUID()

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    var user: User? = null

    var counter: Int = 5

    var deadline: LocalDateTime = LocalDateTime.now().plusMinutes(5)

    var code: String = getRandomString(5)

    constructor(id: UUID, user: User, code: String) {
        this.id = id
        this.user = user
        this.code = code
    }

    constructor(user: User) : super() {
        this.user = user
    }

    private fun getRandomString(length: Int): String {
        val allowedChars = ('0'..'9')
        return (1..length)
            .map { allowedChars.random() }
            .joinToString("")
    }
}
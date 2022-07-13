package it.polito.em280048.registrationservice.config

import it.polito.em280048.registrationservice.entities.Role
import it.polito.em280048.registrationservice.entities.State
import it.polito.em280048.registrationservice.entities.User
import it.polito.em280048.registrationservice.repositories.UserRepository
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class InitializationBean @Autowired constructor(
    private val userRepository: UserRepository,
    private val encoder: PasswordEncoder
): InitializingBean {
    @Value("\${registrationService.app.firstUser}")
    private lateinit var userInitial: String
    @Value("\${registrationService.app.firstPass}")
    private lateinit var passwordInitial: String

    override fun afterPropertiesSet() {
        val user = User(userInitial,encoder.encode(passwordInitial),userInitial,Role.ADMIN)
        user.state = State.ACTIVE
        if (userRepository.findByEmail(userInitial) == null) {
            userRepository.save(user)
        }
    }
}
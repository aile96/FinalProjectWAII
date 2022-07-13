package it.polito.em280048.registrationservice.security

import it.polito.em280048.registrationservice.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class UserDetailsServiceImpl(private val userRepository: UserRepository): UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails? {
        return UserDetailsImpl.build(userRepository.findByEmail(email)
            ?: throw UsernameNotFoundException("User Not Found with email: $email")
        )
    }
    fun loadUserByUId(id: Long): UserDetails? {
        return UserDetailsImpl.build(userRepository.findById(id).orElseThrow { UsernameNotFoundException("User Not Found with email: $id") })
    }
}
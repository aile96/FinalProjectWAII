package it.polito.em280048.registrationservice.services

import it.polito.em280048.registrationservice.dtos.UserDTO
import it.polito.em280048.registrationservice.dtos.UserLoginDTO
import it.polito.em280048.registrationservice.dtos.UserRegisterResponseDTO
import it.polito.em280048.registrationservice.dtos.toRegisterResponseDTO
import it.polito.em280048.registrationservice.entities.Email
import it.polito.em280048.registrationservice.entities.Role
import it.polito.em280048.registrationservice.entities.State
import it.polito.em280048.registrationservice.entities.User
import it.polito.em280048.registrationservice.repositories.ActivationRepository
import it.polito.em280048.registrationservice.repositories.UserRepository
import it.polito.em280048.registrationservice.security.JwtUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.context.SecurityContextHolder
import it.polito.em280048.registrationservice.security.UserDetailsImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.BindingResult
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val activationRepository: ActivationRepository,
    private val emailService: EmailService,
    private val encoder: PasswordEncoder,
    private val jwtUtils: JwtUtils
){
    @Value("\${registrationService.app.firstUser}")
    private lateinit var userInitial: String

    @PreAuthorize("hasAuthority('ADMIN')")
    fun registerMachine(id: String, password: String) {
        val user = User(id,encoder.encode(password),id,Role.MACHINE)
        user.state = State.ACTIVE
        userRepository.save(user)
    }

    fun authMachine(id: String, password: String): String {
        val machine = userRepository.findByEmail(id) ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        if (machine.role!=Role.MACHINE)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST)
        if (!encoder.matches(password, machine.password))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "wrong password")
        return jwtUtils.createToken(machine)
    }

    fun registerUser(userDTO: UserDTO, role: Role, bindingResult: BindingResult): UserRegisterResponseDTO {
        if ( bindingResult.hasFieldErrors("password") )
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "password doesn't follow the rules.")
        if ( bindingResult.hasFieldErrors("email") )
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "email doesn't follow the rules.")
        if ( bindingResult.hasFieldErrors("nickname") )
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "username cannot be empty")
        if(userRepository.findByEmail(userDTO.email) == null
            /*&& userRepository.findByUsername(userDTO.nickname) == null*/){
            if(role==Role.ADMIN) {
                val find = (SecurityContextHolder.getContext().authentication.principal as UserDetailsImpl?)?.id ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "user must exists")
                val u = userRepository.findById(find).orElseThrow {
                    ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "user must exists"
                    )
                }
                if (u.role!=Role.ADMIN)
                    throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "user must be an admin")
            }
            val user = User(userDTO.email, encoder.encode(userDTO.password), userDTO.nickname, role)
            val createdUser = userRepository.save(user)
            val activation = activationRepository.findByUserId(createdUser.id)!![0]
            val emailDTO = Email(
                to = user.email,
                subject = "Activation code",
                activationCode = activation.code,
                provisionalId = activation.id.toString()
            )
            if (emailService.sendMail(emailDTO))
                return createdUser.toRegisterResponseDTO()
            else
                throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "service unavailable")
        }
        else
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "email already exists.")
    }

    fun login(user: UserLoginDTO, bindingResult: BindingResult): String {
        if (bindingResult.hasErrors() && user.email!=userInitial)
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "wrong parameter")
        val u = userRepository.findByEmail(user.email) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "user not found")
        if (!encoder.matches(user.password, u.password))
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "wrong password")
        return jwtUtils.createToken(u)
    }

    @Scheduled(fixedRate = 3000)
    @Async
    fun deleteDanglingUsers(){
        userRepository.deleteDanglingUsers()
    }
}
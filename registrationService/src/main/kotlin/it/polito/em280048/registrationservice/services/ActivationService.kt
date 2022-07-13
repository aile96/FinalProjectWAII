package it.polito.em280048.registrationservice.services

import it.polito.em280048.registrationservice.dtos.ActivationDTO
import it.polito.em280048.registrationservice.dtos.toValidateCodeResponseDTO
import it.polito.em280048.registrationservice.entities.Activation
import it.polito.em280048.registrationservice.entities.State
import it.polito.em280048.registrationservice.entities.User
import it.polito.em280048.registrationservice.repositories.ActivationRepository
import it.polito.em280048.registrationservice.repositories.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class ActivationService {
    @Autowired
    lateinit var activationRepository: ActivationRepository

    @Autowired
    lateinit var userRepository: UserRepository

    fun validateCode(activationDTO: ActivationDTO): ResponseEntity<Any> {
        val optionalActivation = activationRepository.findById(activationDTO.provisional_id)

        lateinit var activation: Activation
        lateinit var user: User

        if(optionalActivation.isPresent){
            activation = optionalActivation.get()
            val optionalUser = userRepository.findById(activation.user!!.id)
            user = optionalUser.get()
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Provisional random ID does not exist")
        }
        if(activation.deadline < LocalDateTime.now()){
            activationRepository.delete(activation.id)
           // throw ResponseStatusException( HttpStatus.NOT_FOUND,"The request is received after the expiration of the deadline")
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The request is received after the expiration of the deadline")
        }
        else if(activation.code != activationDTO.activation_code){
            if(activation.counter == 0 ){
                //it will delete from both tables
                userRepository.delete(user)
            }else{
                activation.counter--
                activationRepository.save(activation)
            }
          //  throw ResponseStatusException( HttpStatus.NOT_FOUND,"Activation code does not match the expected one")
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation code does not match the expected one")
        }
        else{
            user.state = State.ACTIVE
            user = userRepository.save(user)
            activationRepository.delete(activation.id)
            return ResponseEntity.status(HttpStatus.CREATED).body(user.toValidateCodeResponseDTO())
        }
    }
}
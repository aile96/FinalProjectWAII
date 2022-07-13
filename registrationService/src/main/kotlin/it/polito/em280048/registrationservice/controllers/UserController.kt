package it.polito.em280048.registrationservice.controllers

import it.polito.em280048.registrationservice.dtos.*
import it.polito.em280048.registrationservice.entities.Role
import it.polito.em280048.registrationservice.services.ActivationService
import it.polito.em280048.registrationservice.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
class UserController(val userService: UserService, val activationService: ActivationService) {
    @PostMapping("/user/register")
    fun registerUser(@RequestBody @Valid userDTO: UserDTO, result: BindingResult): ResponseEntity<UserRegisterResponseDTO> {
        return ResponseEntity.ok().body(userService.registerUser(userDTO, Role.USER, result))
    }

    @PostMapping("/user/validate")
    fun validateCode(@RequestBody activationDTO: ActivationDTO): ResponseEntity<ValidateCodeResponseDTO> {
        val validationResult = activationService.validateCode(activationDTO)
        if (validationResult.statusCode == HttpStatus.CREATED)
            return ResponseEntity<ValidateCodeResponseDTO>(validationResult.body as ValidateCodeResponseDTO, HttpStatus.CREATED)
        else
            throw ResponseStatusException( HttpStatus.NOT_FOUND, validationResult.body.toString())
    }

    @PostMapping("/admin/register")
    @PreAuthorize("hasAuthority('ADMIN')")
    fun registerAdmin(@RequestBody @Valid userDTO: UserDTO, result: BindingResult): ResponseEntity<Any> {
        return ResponseEntity.ok().body(userService.registerUser(userDTO, Role.ADMIN, result))
    }

    @PostMapping("/user/login")
    fun login(@RequestBody @Valid userLoginDTO: UserLoginDTO, result: BindingResult, response: HttpServletResponse): ResponseEntity<String> {
        response.addHeader("Authorization",userService.login(userLoginDTO, result))
        return ResponseEntity.ok("Success")
    }

    @PostMapping("/machine/login")
    fun authMachine(machineDTO: MachineDTO, response: HttpServletResponse): ResponseEntity<String> {
        response.addHeader("Authorization",userService.authMachine(machineDTO.id,machineDTO.password))
        return ResponseEntity.ok("Success")
    }

    @PostMapping("/machine/register")
    fun regMachine(machineDTO: MachineDTO): String {
        userService.registerMachine(machineDTO.id,machineDTO.password)
        return "Success"
    }
}
package it.polito.em280048.registrationservice.entities

import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Email

@Entity
@Table(name = "users")
data class User(@NotBlank @Email var email: String, @NotBlank var password: String, @NotBlank var username: String, @NotBlank @Enumerated(EnumType.STRING) var role: Role) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0L

    var state: State = State.PENDING

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL])
    var activation: Activation = Activation(this)
}

enum class State {
    PENDING, ACTIVE
}

enum class Role {
    ADMIN, USER, MACHINE
}
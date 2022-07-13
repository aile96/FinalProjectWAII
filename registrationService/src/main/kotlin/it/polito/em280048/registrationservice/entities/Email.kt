package it.polito.em280048.registrationservice.entities

data class Email(
    val to: String,
    val subject: String,
    val activationCode: String,
    val provisionalId: String
)

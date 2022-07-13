package it.polito.em280048.registrationservice.services

import it.polito.em280048.registrationservice.entities.Email
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import javax.mail.internet.MimeMessage


@Service
class EmailService(private var emailSender: JavaMailSender){

    fun sendMail(email: Email):Boolean {
        val msg =createSimpleMessage(email)
        return try {
            emailSender.send(msg)
            true

        }catch (err: Exception){
            println(err)
            false
        }
    }
    private fun createSimpleMessage(email: Email): MimeMessage {
        val message: MimeMessage = emailSender.createMimeMessage()
        val helper = MimeMessageHelper(message,true)

        setupMessage(helper, email)

        return message
    }

    private fun setupMessage(helper: MimeMessageHelper, email: Email) {
        helper.setTo(email.to)
        helper.setSubject(email.subject)
        helper.setText("","<h3>This is your activation code:</h3> <h2>${email.activationCode}</h2>")
    }

}
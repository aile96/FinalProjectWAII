package it.polito.em280048.registrationservice

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class RegistrationServiceApplication

@Configuration
class InitJavaMail {
    @Value("\${spring.mail.host}")
    lateinit var host: String
    @Value("\${spring.mail.port}")
    lateinit var port: String
    @Value("\${spring.mail.username}")
    lateinit var username: String
    @Value("\${spring.mail.password}")
    lateinit var password: String

    @Bean
    fun getJavaMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()
        mailSender.host = host
        mailSender.port = port.toInt()
        mailSender.username = username
        mailSender.password = password
        val props: Properties = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = "true"
        props["mail.smtp.starttls.enable"] = "true"
        props["mail.debug"] = "true"
        return mailSender
    }
}

fun main(args: Array<String>) {
    runApplication<RegistrationServiceApplication>(*args)
}

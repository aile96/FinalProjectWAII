package it.polito.em280048.eurekaserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@EnableEurekaServer
@SpringBootApplication
class EurekaServerApplication

fun main(args: Array<String>) {
	runApplication<EurekaServerApplication>(*args)
}

package it.polito.em280048.travelerservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class TravelerServiceApplication

fun main(args: Array<String>) {
	runApplication<TravelerServiceApplication>(*args)
}

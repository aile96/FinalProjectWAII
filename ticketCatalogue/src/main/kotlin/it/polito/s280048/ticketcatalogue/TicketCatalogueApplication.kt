package it.polito.s280048.ticketcatalogue

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@SpringBootApplication
@EnableDiscoveryClient
class TicketCatalogueApplication

fun main(args: Array<String>) {
	runApplication<TicketCatalogueApplication>(*args)
}

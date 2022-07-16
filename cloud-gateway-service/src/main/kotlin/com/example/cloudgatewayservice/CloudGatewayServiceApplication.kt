package com.example.cloudgatewayservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@SpringBootApplication
@EnableEurekaClient
class CloudGatewayServiceApplication


fun main(args: Array<String>) {
    runApplication<CloudGatewayServiceApplication>(*args)
}

package io.openfuture.openmessenger

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class OpenMessengerApplication

fun main(args: Array<String>) {
    SpringApplication.run(OpenMessengerApplication::class.java, *args)
}


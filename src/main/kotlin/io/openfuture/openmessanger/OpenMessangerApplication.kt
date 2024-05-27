package io.openfuture.openmessanger

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class OpenMessangerApplication

fun main(args: Array<String>) {
    SpringApplication.run(OpenMessangerApplication::class.java, *args)
}


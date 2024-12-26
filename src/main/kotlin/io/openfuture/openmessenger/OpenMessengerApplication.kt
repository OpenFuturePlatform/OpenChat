package io.openfuture.openmessenger

import io.openfuture.openmessenger.service.AssistantService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan

@EntityScan("io.openfuture.openmessenger.repository.entity")
@SpringBootApplication
class OpenMessengerApplication(val assistantService: AssistantService): CommandLineRunner {
    override fun run(vararg args: String?) {
//        val generateNotes = aiProcessor.generateNotes(AiRequest(33, false, LocalDateTime.now().minusDays(13), LocalDateTime.now()))
//        print(generateNotes)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(OpenMessengerApplication::class.java, *args)
}


package io.openfuture.openmessenger

import io.openfuture.openmessenger.service.AiProcessor
import io.openfuture.openmessenger.service.dto.AiRequest
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import java.time.LocalDateTime

@EntityScan("io.openfuture.openmessenger.repository.entity")
@SpringBootApplication
class OpenMessengerApplication(val aiProcessor: AiProcessor): CommandLineRunner {
    override fun run(vararg args: String?) {
//        val generateNotes = aiProcessor.generateNotes(AiRequest(33, false, LocalDateTime.now().minusDays(13), LocalDateTime.now()))
//        print(generateNotes)
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(OpenMessengerApplication::class.java, *args)
}


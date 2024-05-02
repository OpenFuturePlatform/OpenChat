package io.openfuture.openmessanger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.openfuture.openmessanger.assistant.gemini.GeminiService;

@SpringBootApplication
public class OpenMessangerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(OpenMessangerApplication.class, args);
    }

    @Autowired
    private GeminiService geminiService;

    @Override
    public void run(final String... args) throws Exception {
        geminiService.chat("123232312321");
    }

}

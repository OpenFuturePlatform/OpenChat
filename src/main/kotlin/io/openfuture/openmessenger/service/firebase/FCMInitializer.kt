package io.openfuture.openmessenger.service.firebase

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import java.io.IOException
import javax.annotation.PostConstruct

@Service
class FCMInitializer {

    @Value("\${app.firebase-configuration-file}")
    private val firebaseConfigPath: String? = null

    var logger: Logger = LoggerFactory.getLogger(FCMInitializer::class.java)

    @PostConstruct
    fun initialize() {
        try {
            val options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(ClassPathResource(firebaseConfigPath!!).getInputStream()))
                .build()
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options)
                logger.info("Firebase application has been initialized")
            }
        } catch (e: IOException) {
            logger.error(e.message)
        }
    }

}
package io.openfuture.openmessenger.configuration

import io.openfuture.openmessenger.configuration.property.StateProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient


@Configuration
class StateConfig {

    @Bean
    fun stateClient(stateProperties: StateProperties): WebClient =
        WebClient.builder()
            .baseUrl(stateProperties.baseUrl!!)
            .build()

}

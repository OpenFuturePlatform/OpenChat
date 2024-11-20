package io.openfuture.openmessenger.kurento

import org.kurento.client.KurentoClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean

@Configuration
@EnableWebSocket
class KurentoWebsocketConfigurer : WebSocketConfigurer {
    @Bean
    fun handler(): HelloWorldHandler {
        return HelloWorldHandler()
    }

    @Bean
    fun kurentoClient(): KurentoClient {
        return KurentoClient.create()
    }

    @Bean
    fun createServletServerContainerFactoryBean(): ServletServerContainerFactoryBean {
        val container = ServletServerContainerFactoryBean()
        container.setMaxTextMessageBufferSize(32768)
        return container
    }

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(handler(), "/helloworld").setAllowedOriginPatterns("*")
    }
}


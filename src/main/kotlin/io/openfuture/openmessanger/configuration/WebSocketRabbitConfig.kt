package io.openfuture.openmessanger.configuration

import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

//@Configuration
//@ConditionalOnBean(value = WebSocketMessageBrokerConfigurer.class)
class WebSocketRabbitConfig : WebSocketMessageBrokerConfigurer {
    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS()
    }

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableStompBrokerRelay("/exchange")
            .setRelayHost("localhost")
            .setRelayPort(61613)
            .setClientLogin("guest")
            .setClientPasscode("guest")
            .setSystemLogin("guest")
            .setSystemPasscode("guest")
            .setVirtualHost("/")
            .setUserDestinationBroadcast("/topic/user")
            .setUserRegistryBroadcast("/topic/registry")
            .setSystemHeartbeatReceiveInterval(4000)
            .setSystemHeartbeatSendInterval(4000)
        registry.setApplicationDestinationPrefixes("/app")
    }
}
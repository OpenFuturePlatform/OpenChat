package io.openfuture.openmessenger.kurento

import io.openfuture.openmessenger.assistant.gemini.GeminiService
import io.openfuture.openmessenger.kurento.groupcall.CallHandler
import io.openfuture.openmessenger.kurento.groupcall.RoomManager
import io.openfuture.openmessenger.kurento.recording.RecordingCallHandler
import io.openfuture.openmessenger.repository.MeetingNoteRepository
import io.openfuture.openmessenger.service.RecordingManagementService
import io.openfuture.openmessenger.service.SpeechToTextService
import org.kurento.client.KurentoClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean

@Configuration
@EnableWebSocket
class KurentoWebsocketConfigurer(
    @Value("\${kms.url}")
    val kurentoUrl: String,
    val recordingManagementService: RecordingManagementService,
    private val speechToTextService: SpeechToTextService,
    private val geminiService: GeminiService,
    private val meetingNoteRepository: MeetingNoteRepository
) : WebSocketConfigurer {
    @Bean
    fun handler(): HelloWorldHandler {
        return HelloWorldHandler()
    }

    @Bean
    fun kurentoClient(): KurentoClient {
        return KurentoClient.create(kurentoUrl)
    }

    @Bean
    fun createServletServerContainerFactoryBean(): ServletServerContainerFactoryBean {
        val container = ServletServerContainerFactoryBean()
        container.setMaxTextMessageBufferSize(32768)
        return container
    }

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(groupCallHandler(), "/groupcall").setAllowedOriginPatterns("*")
        registry.addHandler(handler(), "/helloworld").setAllowedOriginPatterns("*")
        registry.addHandler(callHandler(), "/call").setAllowedOrigins("*")
    }

    @Bean
    fun registry(): UserRegistry {
        return UserRegistry()
    }

    @Bean
    fun recordingUserRegistry(): io.openfuture.openmessenger.kurento.recording.UserRegistry {
        return io.openfuture.openmessenger.kurento.recording.UserRegistry()
    }

    @Bean
    fun roomManager(): RoomManager {
        return RoomManager()
    }

    @Bean
    fun groupCallHandler(): CallHandler {
        return CallHandler()
    }

    @Bean
    fun callHandler(): RecordingCallHandler {
        return RecordingCallHandler(
            kurentoClient(), recordingUserRegistry(), recordingManagementService, speechToTextService, geminiService, meetingNoteRepository
        )
    }

}


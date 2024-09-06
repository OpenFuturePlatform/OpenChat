package io.openfuture.openmessenger.service.firebase

import com.google.firebase.messaging.*
import io.openfuture.openmessenger.service.dto.PushNotificationRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.ExecutionException

@Service
class FCMService {
    private val logger = LoggerFactory.getLogger(FCMService::class.java)

    @Throws(InterruptedException::class, ExecutionException::class)
    fun sendMessage(data: Map<String, String>, request: PushNotificationRequest) {
        val message = getPreconfiguredMessageWithData(data, request)
        val response = sendAndGetResponse(message)
        logger.info(("Sent message with data. Topic: " + request.topic) + ", " + response)
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    fun sendMessageWithoutData(request: PushNotificationRequest) {
        val message = getPreconfiguredMessageWithoutData(request)
        val response = sendAndGetResponse(message)
        logger.info(("Sent message without data. Topic: " + request.topic) + ", " + response)
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    fun sendMessageToToken(data: Map<String, String>, request: PushNotificationRequest) {
        val message = getPreconfiguredMessageToToken(data, request)
        val response = sendAndGetResponse(message)
        logger.info(("Sent message to token. Device token: " + request.token) + ", " + response)
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    private fun sendAndGetResponse(message: Message): String {
        return FirebaseMessaging.getInstance().sendAsync(message).get()
    }

    @Throws(ExecutionException::class, InterruptedException::class)
    fun subscribeTopic(tokens: List<String?>?, topic: String?) {
        FirebaseMessaging.getInstance().subscribeToTopicAsync(tokens, topic).get()
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    fun unsubscribeTopic(tokens: List<String?>?, topic: String?): TopicManagementResponse {
        return FirebaseMessaging.getInstance().unsubscribeFromTopicAsync(tokens, topic).get()
    }

    private fun getAndroidConfig(topic: String): AndroidConfig {
        return AndroidConfig.builder()
            .setTtl(Duration.ofMinutes(2).toMillis()).setCollapseKey(topic)
            .setPriority(AndroidConfig.Priority.HIGH)
            .setNotification(
                AndroidNotification.builder()
                    .setSound(NotificationParameter.SOUND.value)
                    .setColor(NotificationParameter.COLOR.value)
                    .setTag(topic).build()
            )
            .build()
    }

    private fun getApnsConfig(topic: String): ApnsConfig {
        return ApnsConfig.builder()
            .setAps(Aps.builder().setCategory(topic).setThreadId(topic).build())
            .build()
    }

    private fun getPreconfiguredMessageToToken(data: Map<String, String>, request: PushNotificationRequest): Message {
        return getPreconfiguredMessageBuilder(request)
            .putAllData(data)
            .setToken(request.token)
            .build()
    }

    private fun getPreconfiguredMessageWithoutData(request: PushNotificationRequest): Message {
        return getPreconfiguredMessageBuilder(request)
            .setTopic(request.topic)
            .build()
    }

    private fun getPreconfiguredMessageWithData(data: Map<String, String>, request: PushNotificationRequest): Message {
        return getPreconfiguredMessageBuilder(request)
            .putAllData(data)
            .setTopic(request.topic)
            .build()
    }

    private fun getPreconfiguredMessageBuilder(request: PushNotificationRequest): Message.Builder {
        val androidConfig = getAndroidConfig(request.topic)
        val apnsConfig = getApnsConfig(request.topic)
        return Message.builder()
            .setApnsConfig(apnsConfig)
            .setAndroidConfig(androidConfig)
            .setNotification(
                Notification.builder().setTitle(request.title).setBody(request.message).build()
            )
    }
}
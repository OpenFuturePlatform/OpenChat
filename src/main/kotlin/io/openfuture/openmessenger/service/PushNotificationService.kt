package io.openfuture.openmessenger.service


import io.openfuture.openmessenger.service.dto.PushNotificationRequest
import io.openfuture.openmessenger.service.firebase.FCMService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ExecutionException


@Service
class PushNotificationService(
    private val fcmService: FCMService
) {
    private val logger = LoggerFactory.getLogger(PushNotificationService::class.java)

    fun subscribe(tokens: List<String>, topic: String) {
        try {
            fcmService.subscribeTopic(tokens, topic)
        } catch (e: InterruptedException) {
            logger.error(e.message)
        } catch (e: ExecutionException) {
            logger.error(e.message)
        }
    }

    fun sendPushNotificationWithoutData(request: PushNotificationRequest?) {
        try {
            fcmService.sendMessageWithoutData(request!!)
        } catch (e: InterruptedException) {
            logger.error(e.message)
        } catch (e: ExecutionException) {
            logger.error(e.message)
        }
    }

    fun sendPushNotificationToToken(
        request: PushNotificationRequest
    ) {
        try {
            fcmService.sendMessageToToken(mapOf(), request)
        } catch (e: InterruptedException) {
            logger.error(e.message)
        } catch (e: ExecutionException) {
            logger.error(e.message)
        }
    }

//    private fun toPushData(notificationCreateRequest: NotificationCreateRequest): Map<String, String> {
//        val pushData: MutableMap<String, String> = HashMap()
//        val random = Random()
//        pushData["messageId"] = abs(random.nextInt().toDouble()).toString()
//        pushData["text"] = LocalDateTime.now().toString()
//        pushData["title"] = notificationCreateRequest.getTitle()
//        pushData["url"] = notificationCreateRequest.getUrl()
//        pushData["click_action"] = "KMP_NOTIFICATION_CLICK"
//        pushData["status"] = notificationCreateRequest.getStatus().toString()
//        pushData["notificationType"] = notificationCreateRequest.getNotificationType().toString()
//        return pushData
//    }


}
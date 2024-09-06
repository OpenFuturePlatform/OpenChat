package io.openfuture.openmessenger.web.controller

import io.openfuture.openmessenger.component.state.DefaultStateApi
import io.openfuture.openmessenger.repository.UserFirebaseTokenRepository
import io.openfuture.openmessenger.service.PushNotificationService
import io.openfuture.openmessenger.service.UserAuthService
import io.openfuture.openmessenger.service.WalletManagementService
import io.openfuture.openmessenger.service.dto.SaveWalletRequest
import io.openfuture.openmessenger.service.dto.DecryptWalletRequest
import io.openfuture.openmessenger.service.dto.PushNotificationRequest
import io.openfuture.openmessenger.service.response.WalletResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/wallets")
@RestController
class WalletController(
    val userAuthService: UserAuthService,
    val stateApi: DefaultStateApi,
    val walletManagementService: WalletManagementService,
    val pushNotificationService: PushNotificationService,
    val userFirebaseTokenRepository: UserFirebaseTokenRepository
) {

    @PostMapping("/save")
    fun saveWallet(
        @RequestBody request: SaveWalletRequest
    ): WalletResponse {
        val currentUser = userAuthService.current()
        val wallet = walletManagementService.saveWallet(request, currentUser.email!!)
        stateApi.createWallet(wallet.address!!, "http://localhost:5001/api/v1/wallets/webhook", wallet.blockchainType, "chatx")

        return wallet
    }

    /*@PostMapping("/webhook")
    fun processWebHook(
        @RequestBody webhookResponse: WebhookResponse
    ) {
        val pushNotificationRequest = PushNotificationRequest("test", "message", "topic", webhookResponse.message)
        println("WebHook response $webhookResponse")
        pushNotificationService.sendPushNotificationToToken(pushNotificationRequest)
    }*/

    @PostMapping("/notification")
    fun processWebHook(
        @RequestBody notificationCreate: NotificationCreate
    ) {
        val tokens = userFirebaseTokenRepository.findAllByUserId(notificationCreate.userId)
        for (token in tokens) {
            val pushNotificationRequest = PushNotificationRequest(notificationCreate.title, notificationCreate.message!!, "topic", token.firebaseToken)
            pushNotificationService.sendPushNotificationToToken(pushNotificationRequest)
        }
    }

    @GetMapping
    fun get(): List<WalletResponse> {
        val currentUser = userAuthService.current()
        return walletManagementService.getByUserId(currentUser.email!!)
    }

    data class WebhookResponse(
        val status: HttpStatus,
        val url: String,
        val message: String?
    )

    data class NotificationCreate(
        val userId: String,
        val title: String,
        val message: String?
    )
}

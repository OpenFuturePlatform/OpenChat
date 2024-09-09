package io.openfuture.openmessenger.service


import io.openfuture.openmessenger.repository.UserFirebaseTokenRepository
import io.openfuture.openmessenger.repository.WalletRepository
import io.openfuture.openmessenger.repository.entity.WalletEntity
import io.openfuture.openmessenger.service.dto.PushNotificationRequest
import io.openfuture.openmessenger.service.dto.SaveWalletRequest
import io.openfuture.openmessenger.service.dto.WebhookPayloadDto
import io.openfuture.openmessenger.service.response.WalletResponse
import org.springframework.stereotype.Service


@Service
class WalletManagementService(
    private val walletRepository: WalletRepository,
    private val pushNotificationService: PushNotificationService,
    private val userFirebaseTokenRepository: UserFirebaseTokenRepository
) {
    fun saveWallet(request: SaveWalletRequest, username: String): WalletResponse {

        // Save address on db
        val wallet = walletRepository.save(WalletEntity(
            request.address.lowercase(),
            request.blockchainType,
            username
        ))

        return WalletResponse(wallet.address, wallet.balance, request.blockchainType)
    }


    fun getByUserId(userId: String): List<WalletResponse> {
        return walletRepository.findAllByUserId(userId)
            .map { WalletResponse(it.address, it.balance, it.blockchainType) }
    }

    fun processWebHook(webhookPayloadDto: WebhookPayloadDto){
        println("State webhook $webhookPayloadDto")
        val walletEntity = walletRepository.findFirstByAddress(webhookPayloadDto.walletAddress)
        walletEntity.let { entity ->
            println("State wallet entity")
            val fireBaseTokens = userFirebaseTokenRepository.findAllByUserId(entity?.userId!!)
            println("WebHook response $webhookPayloadDto")
            for (token in fireBaseTokens) {
                val message = "New transaction from ${webhookPayloadDto.transaction.from} to ${webhookPayloadDto.walletAddress} with amount ${webhookPayloadDto.transaction.amount}"
                val pushNotificationRequest = PushNotificationRequest("Transfer", message, "topic", token.firebaseToken)
                pushNotificationService.sendPushNotificationToToken(pushNotificationRequest)
            }
        }
    }

}
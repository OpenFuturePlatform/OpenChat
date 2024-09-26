package io.openfuture.openmessenger.service


import io.openfuture.openmessenger.repository.UserFirebaseTokenRepository
import io.openfuture.openmessenger.repository.WalletRepository
import io.openfuture.openmessenger.repository.entity.UserFireBaseToken
import io.openfuture.openmessenger.repository.entity.WalletEntity
import io.openfuture.openmessenger.service.dto.PushNotificationRequest
import io.openfuture.openmessenger.service.dto.SaveWalletRequest
import io.openfuture.openmessenger.service.dto.WebhookPayloadDto
import io.openfuture.openmessenger.service.response.WalletResponse
import org.springframework.stereotype.Service
import java.math.BigDecimal


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

            val initialBalance : BigDecimal = entity?.balance?.toBigDecimal() ?: BigDecimal.ZERO
            entity?.balance = initialBalance.plus(webhookPayloadDto.transaction.amount).toString()
            println("Entity $entity")
            walletRepository.save(entity!!)

            val fireBaseTokens = userFirebaseTokenRepository.findAllByUserId(entity.userId!!)
            println("WebHook response $webhookPayloadDto")
            for (token in fireBaseTokens) {
                sendNotificationToToken(webhookPayloadDto, token)
            }
        }
    }

    private fun sendNotificationToToken(
        webhookPayloadDto: WebhookPayloadDto,
        token: UserFireBaseToken
    ) {
        val blockchainType = when (webhookPayloadDto.blockchain) {
            "EthereumBlockchain", "GoerliBlockchain" -> "ETH"
            "BinanceBlockchain", "BinanceTestnetBlockchain" -> "BNB"
            "TronBlockchain", "TronShastaBlockchain" -> "TRX"
            "BitcoinBlockchain" -> "BTC"
            else -> "ETH"
        }
        val message =
            "New transaction ${webhookPayloadDto.walletAddress.take(4)}..${webhookPayloadDto.walletAddress.takeLast(2)} - ${webhookPayloadDto.transaction.amount} $blockchainType"
        val pushNotificationRequest =
            PushNotificationRequest("Transfer", message, "transaction_webhook", token.firebaseToken)
        pushNotificationService.sendPushNotificationToToken(pushNotificationRequest)
    }

}
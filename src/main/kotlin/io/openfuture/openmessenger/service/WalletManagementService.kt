package io.openfuture.openmessenger.service


import io.openfuture.openmessenger.repository.WalletRepository
import io.openfuture.openmessenger.repository.entity.WalletEntity
import io.openfuture.openmessenger.service.dto.SaveWalletRequest
import io.openfuture.openmessenger.service.response.WalletResponse
import org.springframework.stereotype.Service


@Service
class WalletManagementService(
    private val walletRepository: WalletRepository
) {
    fun saveWallet(request: SaveWalletRequest, username: String): WalletResponse {

        // Save address on db
        val wallet = walletRepository.save(WalletEntity(
            request.address,
            request.blockchainType,
            username
        ))

        return WalletResponse(wallet.address, wallet.balance, request.blockchainType)
    }


    fun getByUserId(userId: String): List<WalletResponse> {
        return walletRepository.findAllByUserId(userId)
            .map { WalletResponse(it.address, it.balance, it.blockchainType) }
    }

}
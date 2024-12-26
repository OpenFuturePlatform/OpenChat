package io.openfuture.openmessenger.component.state

import io.openfuture.openmessenger.repository.entity.BlockchainType
import io.openfuture.openmessenger.service.dto.StateWalletDto

interface StateApi {
    fun createWallet(address: String, webHook: String, blockchain: BlockchainType, applicationId: String): StateWalletDto?
}

package io.openfuture.openmessenger.service.dto

import io.openfuture.openmessenger.repository.entity.BlockchainType

data class SaveWalletRequest(
    var blockchainType: BlockchainType,
    var address: String,
    var userId: String
)

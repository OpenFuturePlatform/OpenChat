package io.openfuture.openmessenger.service.dto

import io.openfuture.openmessenger.repository.entity.BlockchainType

data class CreateWalletRequest(
    var blockchainType: BlockchainType,
    var password: String
)

package io.openfuture.openmessenger.service.response

import io.openfuture.openmessenger.repository.entity.BlockchainType


data class WalletResponse(
    var address: String? = null,
    var balance: String? = null,
    var blockchainType: BlockchainType
)
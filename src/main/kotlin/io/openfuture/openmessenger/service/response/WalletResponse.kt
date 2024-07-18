package io.openfuture.openmessenger.service.response

import io.openfuture.openmessenger.repository.entity.BlockchainType


data class WalletResponse(
    var address: String? = null,
    var privateKey: String? = null,
    var blockchainType: BlockchainType,
    var seedPhrases: String? = null,
)
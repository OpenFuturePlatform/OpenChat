package io.openfuture.openmessenger.repository.entity

import io.openfuture.openmessenger.repository.entity.base.Dictionary


enum class BlockchainType(
        private val id: Int,
        private val value: String
) : Dictionary {

    ETH(1, "ETH"),
    BTC(2, "BTC"),
    BNB(3, "BNB"),
    TRX(4, "TRX")
    ;

    override fun getId(): Int = id

    fun getValue(): String = value

}
package io.openfuture.openmessenger.repository.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.now

@Entity
@Table(name = "open_wallets")
class WalletEntity() {
    constructor(
        address: String?,
        blockchainType: BlockchainType,
        userId: String?
    ): this() {
        this.address = address
        this.userId = userId
        this.blockchainType = blockchainType
        this.createdAt = now()
        this.updatedAt = now()
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var createdAt: LocalDateTime? = now()
    var updatedAt: LocalDateTime? = now()
    var address: String? = null
    @Enumerated(EnumType.STRING)
    var blockchainType: BlockchainType = BlockchainType.BTC
    var balance: String? = null
    var userId: String? = null
}

package io.openfuture.openmessenger.repository.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.now

@Entity
@Table(name = "blockchain_contracts")
class BlockchainContractEntity() {
    constructor(
        contractAddress: String,
        contractName: String,
        decimal: Int,
        blockchain: BlockchainType,
        isTest: Boolean
    ): this() {
        this.contractName = contractName
        this.contractAddress = contractAddress
        this.blockchain = blockchain
        this.isTest = isTest
        this.decimal = decimal
        this.createdAt = now()
        this.updatedAt = now()
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var createdAt: LocalDateTime? = now()
    var updatedAt: LocalDateTime? = now()
    var isTest: Boolean = false
    @Enumerated(EnumType.STRING)
    var blockchain: BlockchainType = BlockchainType.BTC
    var contractAddress: String? = null
    var contractName: String? = null
    var decimal: Int? = 6
}

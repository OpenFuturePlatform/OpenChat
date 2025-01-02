package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.BlockchainContractEntity
import io.openfuture.openmessenger.repository.entity.BlockchainType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface BlockchainContractRepository : JpaRepository<BlockchainContractEntity, Long> {
    @Query("SELECT t from BlockchainContractEntity t where t.blockchain = :blockchain and t.isTest = :isTest")
    fun findFirstByBlockchain(blockchain: BlockchainType, isTest: Boolean) : BlockchainContractEntity?

    @Query("SELECT t from BlockchainContractEntity t where t.isTest = :isTest")
    fun findAllByIsTest(isTest: Boolean) : List<BlockchainContractEntity>

}
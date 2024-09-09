package io.openfuture.openmessenger.repository

import io.openfuture.openmessenger.repository.entity.WalletEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface WalletRepository : JpaRepository<WalletEntity, Long> {
    @Query("SELECT t from WalletEntity t where t.userId =:userId ")
    fun findAllByUserId(userId: String) : List<WalletEntity>

    @Query("SELECT t from WalletEntity t where lower(t.address) =:address ")
    fun findFirstByAddress(address: String) : WalletEntity?

}
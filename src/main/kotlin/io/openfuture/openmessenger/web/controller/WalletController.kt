package io.openfuture.openmessenger.web.controller

import io.openfuture.openmessenger.repository.entity.WalletEntity
import io.openfuture.openmessenger.service.UserAuthService
import io.openfuture.openmessenger.service.WalletManagementService
import io.openfuture.openmessenger.service.dto.CreateWalletRequest
import io.openfuture.openmessenger.service.dto.DecryptWalletRequest
import io.openfuture.openmessenger.service.response.WalletResponse
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/wallets")
@RestController
class WalletController(
    val userAuthService: UserAuthService,
    val walletManagementService: WalletManagementService
) {

    @PostMapping("/generate")
    fun create(
        @RequestBody request: CreateWalletRequest
    ): WalletResponse {
        val currentUser = userAuthService.current()
        return walletManagementService.generate(request, currentUser.email!!)
    }

    @PostMapping("/decrypt")
    fun create(
        @RequestBody request: DecryptWalletRequest
    ): String {
        return walletManagementService.decryptWallet(request)
    }

    @GetMapping
    fun get(): List<WalletResponse> {
        val currentUser = userAuthService.current()
        return walletManagementService.getByUserId(currentUser.email!!)
    }
}

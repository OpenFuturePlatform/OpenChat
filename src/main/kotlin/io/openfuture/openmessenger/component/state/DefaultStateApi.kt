package io.openfuture.openmessenger.component.state

import io.openfuture.openmessenger.repository.entity.BlockchainType
import io.openfuture.openmessenger.service.dto.StateWalletDto
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient


@Component
class DefaultStateApi(
    private val stateWebClient: WebClient
) : StateApi {

    override fun createWallet(
        address: String,
        webHook: String,
        blockchain: BlockchainType,
        applicationId: String
    ): StateWalletDto? {
        val request = CreateStateWalletRequest(address, applicationId, blockchain.getValue(), webHook)
        println("State save request $request")
        return stateWebClient
            .post()
            .uri("http://localhost:8545/api/wallets/single")
            .accept(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(request))
            .retrieve()
            .bodyToMono(StateWalletDto::class.java)
            .block()
    }

    data class CreateStateWalletRequest(
        val address: String,
        val applicationId: String,
        val blockchain: String,
        val webhook: String
    )
}

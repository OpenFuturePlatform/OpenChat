package io.openfuture.openmessanger.service.dto

import javax.validation.constraints.NotBlank

data class AuthenticatedChallengeRequest(
    private val sessionId: @NotBlank String? = null,
    private val username: @NotBlank(message = "username is mandatory") String? = null,
    private val challengeType: @NotBlank String? = null)

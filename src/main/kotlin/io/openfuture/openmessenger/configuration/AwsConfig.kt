package io.openfuture.openmessenger.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "aws", ignoreUnknownFields = false)
data class AwsConfig(
    var accessKey: String? = null,
    var secretKey: String? = null,
    var region: String? = null,
    var attachmentsBucket: String? = null,
    var cognito: Cognito = Cognito()
)

data class Cognito(
    var userPoolId: String? = null,
    var appClientId: String? = null,
    var appClientSecret: String? = null
)
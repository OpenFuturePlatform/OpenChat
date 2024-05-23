package io.openfuture.openmessanger.domain.enums

enum class CognitoAttributesEnum(val values: String) {
    USERNAME("USERNAME"),
    PASSWORD("PASSWORD"),
    SECRET_HASH("SECRET_HASH"),
    NEW_PASSWORD("NEW_PASSWORD"),
    SMS_MFA_CODE("SMS_MFA_CODE"),
    REFRESH_TOKEN("REFRESH_TOKEN");

    companion object {
        private val lookup: MutableMap<String, CognitoAttributesEnum> = HashMap()

        init {
            for (env in entries) {
                lookup[env.values] = env
            }
        }

        //This method can be used for reverse lookup purpose
        operator fun get(key: String?): CognitoAttributesEnum? {
            return lookup[key]
        }
    }
}
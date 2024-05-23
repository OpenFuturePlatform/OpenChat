package io.openfuture.openmessanger.web.response

data class BaseResponse(val data: Any?, val message: String?, val error: Boolean = true)
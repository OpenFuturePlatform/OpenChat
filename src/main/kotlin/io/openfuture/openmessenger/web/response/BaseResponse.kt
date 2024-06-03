package io.openfuture.openmessenger.web.response

data class BaseResponse(val data: Any?, val message: String?, val error: Boolean = true)
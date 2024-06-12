package io.openfuture.openmessenger.exception

class FailedAuthenticationException : ServiceException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, throwable: Throwable?) : super(message, throwable)
}
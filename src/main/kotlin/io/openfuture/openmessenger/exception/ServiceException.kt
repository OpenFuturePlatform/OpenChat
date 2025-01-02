package io.openfuture.openmessenger.exception

open class ServiceException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(throwable: Throwable?) : super(throwable)
    constructor(message: String?, throwable: Throwable?) : super(message, throwable)
}
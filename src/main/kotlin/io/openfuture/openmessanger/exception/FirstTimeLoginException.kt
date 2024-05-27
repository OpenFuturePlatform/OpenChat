package io.openfuture.openmessanger.exception

class FirstTimeLoginException : ServiceException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, throwable: Throwable?) : super(message, throwable)
}
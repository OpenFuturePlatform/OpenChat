package io.openfuture.openmessenger.exception

class InvalidPasswordException : ServiceException {
    constructor()
    constructor(message: String?) : super(message)
    constructor(message: String?, throwable: Throwable?) : super(message, throwable)
}
package io.openfuture.openmessanger.exception

class UsernameExistsException : ServiceException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, throwable: Throwable?) : super(message, throwable)
}
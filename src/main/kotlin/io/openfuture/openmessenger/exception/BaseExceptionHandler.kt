package io.openfuture.openmessenger.exception

import com.amazonaws.services.cognitoidp.model.InternalErrorException
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException
import io.openfuture.openmessenger.web.response.BaseResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.function.Consumer
import javax.validation.ConstraintViolationException

@RestControllerAdvice
class BaseExceptionHandler {

    companion object{
        private val log: Logger = LoggerFactory.getLogger(BaseExceptionHandler::class.java)
    }

    @ExceptionHandler(FailedAuthenticationException::class, NotAuthorizedException::class, UserNotFoundException::class, InvalidPasswordException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun unauthorizedExceptions(ex: Exception): BaseResponse {
        log.error(ex.message, ex.localizedMessage)
        return BaseResponse(null, ex.message)
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): BaseResponse {
        val errors: MutableMap<String, String?> = HashMap()
        ex.bindingResult.fieldErrors.forEach(Consumer { error: FieldError -> errors[error.field] = error.defaultMessage })
        return BaseResponse(errors, "Validation failed")
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException::class, UsernameExistsException::class, InvalidParameterException::class)
    fun processValidationError(ex: ConstraintViolationException): BaseResponse {
        return BaseResponse(null, ex.message)
    }

    @ExceptionHandler(Exception::class, InternalErrorException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleAllExceptions(ex: Exception): BaseResponse {
        ex.printStackTrace()
        log.error(ex.message, ex.localizedMessage)
        return BaseResponse(null, if (ex.message != null) ex.message else "Oops something went wrong !!!")
    }
}
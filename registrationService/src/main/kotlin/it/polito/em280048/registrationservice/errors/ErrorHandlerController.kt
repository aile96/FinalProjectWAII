package it.polito.em280048.registrationservice.errors

import org.springframework.http.HttpStatus
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ErrorHandlerController {
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun methodNotSupported(e: HttpRequestMethodNotSupportedException): String {
        return "Request not allowed"
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler(NumberFormatException::class)
    fun server(e: NumberFormatException): String {
        return "Server unavailable at the moment"
    }
}

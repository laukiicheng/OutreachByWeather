package com.acme.outreach.controllers

import javax.validation.ConstraintViolationException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

private val logger = KotlinLogging.logger {}

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleBadRequest(constraintViolationException: ConstraintViolationException): ResponseEntity<String> {
        logger.warn {
            """
            Validation error ${ConstraintViolationException::class.simpleName}
            ${constraintViolationException.message}
            """.trimIndent()
        }

        return ResponseEntity("Error: ${constraintViolationException.message}", HttpStatus.BAD_REQUEST)
    }
}

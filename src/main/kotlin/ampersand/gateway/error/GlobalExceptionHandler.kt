package ampersand.gateway.error

import com.nimbusds.jose.shaded.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.annotation.ControllerAdvice

@ControllerAdvice
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = LoggerFactory.getLogger(this.javaClass.name)

    @ExceptionHandler(GatewayException::class)
    fun expectedException(ex: GatewayException): ResponseEntity<ErrorResponse> {
        log.warn("ExpectedException : {} ", ex.message)
        log.trace("ExpectedException Details : ", ex)
        return ResponseEntity.status(ex.httpStatus.value()).body(ErrorResponse.of(ex))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class, HttpMessageNotReadableException::class)
    fun validationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        log.warn("Validation Failed : {}", ex.message)
        log.trace("Validation Failed Details : ", ex)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value())
            .body(ErrorResponse(methodArgumentNotValidExceptionToJson(ex)))
    }

    @ExceptionHandler(RuntimeException::class)
    fun unExpectedException(ex: RuntimeException): ResponseEntity<ErrorResponse> {
        log.error("UnExpectedException Occur : ", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .body(ErrorResponse("internal server error has occurred"))
    }

    private fun methodArgumentNotValidExceptionToJson(ex: MethodArgumentNotValidException): String {
        val globalResults = HashMap<String, Any>()
        val fieldResults = HashMap<String, String>()

        ex.bindingResult.globalErrors.forEach { error ->
            globalResults[ex.bindingResult.objectName] = error.defaultMessage!!
        }
        ex.bindingResult.fieldErrors.forEach { error ->
            fieldResults[error.field] = error.defaultMessage!!
        }
        globalResults[ex.bindingResult.objectName] = fieldResults

        return JSONObject(globalResults).toString().replace("\"", "'")
    }
}

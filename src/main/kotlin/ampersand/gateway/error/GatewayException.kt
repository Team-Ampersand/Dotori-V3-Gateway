package ampersand.gateway.error

import org.springframework.http.HttpStatus

class GatewayException : RuntimeException {

    val httpStatus: HttpStatus

    constructor(message: String, httpStatus: HttpStatus) : super(message) {
        this.httpStatus = httpStatus
    }

    constructor(httpStatus: HttpStatus) : super(httpStatus.reasonPhrase) {
        this.httpStatus = httpStatus
    }

    override fun fillInStackTrace(): Throwable {
        return this
    }

}

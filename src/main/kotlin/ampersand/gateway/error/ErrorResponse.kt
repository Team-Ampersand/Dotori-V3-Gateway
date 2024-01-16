package ampersand.gateway.error

data class ErrorResponse(
    val message: String
) {
    companion object {
        fun of(cause: Throwable): ErrorResponse =
            ErrorResponse(cause.message!!)
    }
}

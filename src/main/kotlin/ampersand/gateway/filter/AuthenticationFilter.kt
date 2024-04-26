package ampersand.gateway.filter

import ampersand.gateway.token.Jwt
import ampersand.gateway.token.JwtParsingSupport.getAuthorizationFromHeader
import ampersand.gateway.token.JwtParsingSupport.removeJwtTokenPrefix
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.cloud.gateway.filter.GlobalFilter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.UUID

@Component
class AuthenticationFilter(
    private val jwt: Jwt
) : GlobalFilter {

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
        val token = request.headers.getAuthorizationFromHeader()
        val requestBuilder = request.mutate()

        requestBuilder.header("Request-Id", UUID.randomUUID().toString())
        token?.let {
            val pureToken = it.removeJwtTokenPrefix()
            val jwtTokenClaims = jwt.parseToken(pureToken)

            val subject = jwtTokenClaims["sub"].toString()
            val authority = jwtTokenClaims["authority"].toString()

            requestBuilder.header("Request-Member-Id", subject)
            requestBuilder.header("Request-Member-Authority", authority)
        }

        val modifiedRequest = requestBuilder.build()

        val modifiedExchange = exchange.mutate()
            .request(modifiedRequest)
            .build()

        return chain.filter(modifiedExchange)

    }

}

package ampersand.gateway.token

import ampersand.gateway.error.GatewayException
import com.nimbusds.jose.crypto.MACVerifier
import com.nimbusds.jwt.SignedJWT
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import java.util.*

@Component
class Jwt(
    private val jwtProperties: JwtProperties
) {

    fun parseToken(token: String): Map<String, Any> {
        val signedJwt = SignedJWT.parse(token)

        if(signedJwt.jwtClaimsSet.expirationTime.before(Date())) {
            throw GatewayException("Jwt is Expired", HttpStatus.UNAUTHORIZED)
        }

        val verifier = MACVerifier(jwtProperties.secretKey)
        if(!signedJwt.verify(verifier)) {
            throw GatewayException("Invalid Jwt", HttpStatus.UNAUTHORIZED)
        }

        return signedJwt.jwtClaimsSet.toJSONObject()
    }
}
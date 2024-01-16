package ampersand.gateway.token

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
class JwtProperties(
    val secretKey: String
)
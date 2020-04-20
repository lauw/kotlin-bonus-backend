package auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.*
import java.util.concurrent.TimeUnit

class SimpleJWT(secret: String, private val audience: String, private val issuer: String) {
    private val validityInMs = TimeUnit.HOURS.toMillis(1)
    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT.require(algorithm).withAudience(audience).withIssuer(issuer).build()

    fun sign(id: Int): String = JWT.create()
            .withClaim("id", id)
            .withAudience(audience)
            .withIssuer(issuer)
            .withExpiresAt(getExpiration())
            .sign(algorithm)

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)
}

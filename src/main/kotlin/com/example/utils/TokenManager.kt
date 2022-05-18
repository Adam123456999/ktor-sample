package com.example.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.UserModel
import io.ktor.server.config.*
import java.util.*

class TokenManager(config: HoconApplicationConfig) {

  private val audience = config.property("jwt.audience").getString()
  private val secret = config.property("jwt.secret").getString()
  private val issuer = config.property("jwt.issuer").getString()
  private val expirationDate = Date(System.currentTimeMillis() + 900000)

  fun generateJWTToken(user: UserModel): String = JWT.create()
      .withAudience(audience)
      .withIssuer(issuer)
      .withClaim("id", user.id)
      .withClaim("username", user.name)
      .withClaim("password", user.password)
      .withExpiresAt(expirationDate)
      .sign(Algorithm.HMAC256(secret))

  fun verifyJWTToken(): JWTVerifier = JWT.require(Algorithm.HMAC256(secret))
    .withAudience(audience)
    .withIssuer(issuer)
    .build()
}
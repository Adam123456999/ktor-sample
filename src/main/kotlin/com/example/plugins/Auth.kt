package com.example.plugins

import com.example.utils.TokenManager
import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.response.*

fun Application.configureAuth(tokenManager: TokenManager) {
  val myRealm = environment.config.property("jwt.realm").getString()

  install(Authentication) {
    basic("auth-basic") {
      realm = "Access to the '/' path"
      validate { credentials ->
        if (credentials.name == "jetbrains" && credentials.password == "foobar") {
          UserIdPrincipal(credentials.name)
        } else {
          null
        }
      }
    }

    jwt("auth-jwt") {
      realm = myRealm

      verifier(tokenManager.verifyJWTToken())

      validate { jwtCredential ->
        if (jwtCredential.payload.getClaim("username").asString().isNotEmpty()) {
          JWTPrincipal(jwtCredential.payload)
        } else {
          null
        }
      }

      challenge {defaultScheme, realm ->
        call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
      }
    }
  }
}
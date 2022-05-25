package com.example.routes

import com.example.db.DatabaseConnection
import com.example.models.EmptyResponse
import com.example.models.UserModel
import com.example.models.UserRequest
import com.example.models.BaseResponse
import com.example.utils.TokenManager
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRouting(db: DatabaseConnection, tokenManager: TokenManager) {
  route("/") {

    post("/login") {
      val principal = call.principal<JWTPrincipal>()
      val expiresAt = principal?.expiresAt?.time?.minus(System.currentTimeMillis())
      call.respond(
        status = HttpStatusCode.OK,
        message = expiresAt.toString()
      )
    }

    post("/refresh") {
      val userModel = call.receive<UserModel>()
      tokenManager.generateJWTToken(userModel)
      call.respond(
        status = HttpStatusCode.OK,
        message = EmptyResponse(success = true)
      )
    }

    post("/register") {
      val userRequest = call.receive<UserRequest>()
      val id = db.generateRandomID()
      val userModel = UserModel(
        id = id,
        name = userRequest.name,
        password = userRequest.password
      )
      db.addUser(userModel)

      val token = tokenManager.generateJWTToken(userModel)

      call.response.cookies.append("jwt-token", token)
      call.respond(
        status = HttpStatusCode.Created,
        message = BaseResponse(
          success = true,
          data = userModel
        )
      )
    }

    authenticate("auth-jwt") {

      get("/hello") {
        val principal = call.principal<JWTPrincipal>()
        val username = principal!!.payload.getClaim("username").asString()
        val expiresAt = principal.expiresAt?.time?.minus(System.currentTimeMillis())
        call.respond(
          status = HttpStatusCode.OK,
          message = BaseResponse(
            success = true,
            data = "Hello, $username! Token is expired at ${expiresAt?.div(60000)} min."
          )
        )
      }

      usersRouting(db)
    }
  }
}
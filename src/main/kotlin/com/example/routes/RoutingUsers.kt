package com.example.routes

import com.example.db.DatabaseConnection
import com.example.models.EmptyResponse
import com.example.models.UserModel
import com.example.models.UserRequest
import com.example.models.BaseResponse
import com.example.utils.TokenManager
import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usersRouting(db: DatabaseConnection) {
  route("api/users") {

    get("{id}") {
      val id = call.parameters["id"]
        ?: return@get call.respond(
          status = HttpStatusCode.BadRequest,
          message = EmptyResponse(success = false)
        )

      val user = db.getUserByID(id)
        ?: return@get call.respond(
          status = HttpStatusCode.NotFound,
          message = EmptyResponse(success = false)
        )

      return@get call.respond(
        status = HttpStatusCode.OK,
        message = BaseResponse(success = true, data = user)
      )
    }

    get {
      val users = db.getAllUsers()
      call.respond(
        status = HttpStatusCode.OK,
        message = BaseResponse(success = true, data = users)
      )
    }

    post {
      val userRequest = call.receive<UserRequest>()
      val id = db.generateRandomID()
      val userModel = UserModel(
        id = id,
        name = userRequest.name,
        password = userRequest.password
      )

      db.addUser(userModel)

      val token = TokenManager(HoconApplicationConfig(ConfigFactory.load())).generateJWTToken(userModel)

      call.response.cookies.append("jwt-token", token)
      call.respond(
        status = HttpStatusCode.Created,
        message = EmptyResponse(success = true)
      )
    }

    delete {
      val result = db.deleteAllUsers()
      call.respond(
        status = HttpStatusCode.OK,
        message = EmptyResponse(success = true)
      )
    }

    delete("{id}") {
      val id = call.parameters["id"]
        ?: return@delete call.respond(
          status = HttpStatusCode.BadRequest,
          message = EmptyResponse(success = false)
        )

      db.deleteUserAtID(id)

      call.respond(
        status = HttpStatusCode.OK,
        message = EmptyResponse(success = true)
      )
    }

    patch("{id}") {
      val userModel = call.receive<UserModel>()
      val id = call.parameters["id"]
        ?: return@patch call.respond(
          status = HttpStatusCode.BadRequest,
          message = EmptyResponse(success = false)
        )

      db.patchUserAtID(userModel, id)
      call.response.status(HttpStatusCode.NotFound)
      call.respond(
        status = HttpStatusCode.NotFound,
        message = EmptyResponse(success = false)
      )
    }
  }
}

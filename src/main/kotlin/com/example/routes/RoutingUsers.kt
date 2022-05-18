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
      val result = db.addUser(userModel)

      if (result == 0) {
        call.respond(
          status = HttpStatusCode.InternalServerError,
          message = EmptyResponse(success = false)
        )
      }

      val token = TokenManager(HoconApplicationConfig(ConfigFactory.load())).generateJWTToken(userModel)

      call.response.cookies.append("jwt-token", token)
      call.respond(
        status = HttpStatusCode.Created,
        message = EmptyResponse(success = true)
      )
    }

    delete {
      val result = db.deleteAllUsers()
      if (result == 0) {
        call.respond(
          status = HttpStatusCode.InternalServerError,
          message = EmptyResponse(success = false)
        )
      } else {
        call.respond(
          status = HttpStatusCode.OK,
          message = EmptyResponse(success = true)
        )
      }
    }

    delete("{id}") {
      val id = call.parameters["id"]
        ?: return@delete call.respond(
          status = HttpStatusCode.BadRequest,
          message = EmptyResponse(success = false)
        )

      val result = db.deleteUserAtID(id)

      if (result == 0) {
        call.respond(
          status = HttpStatusCode.NotFound,
          message = EmptyResponse(success = false)
        )
      } else {
        call.respond(
          status = HttpStatusCode.OK,
          message = EmptyResponse(success = true)
        )
      }
    }

    put("{id}") {
      val userModel = call.receive<UserModel>()
      val id = call.parameters["id"]
        ?: return@put call.respond(
          status = HttpStatusCode.BadRequest,
          message = EmptyResponse(success = false)
        )

      val result = db.putUserAtID(userModel, id)

      if (result == 0) {
        call.respond(
          status = HttpStatusCode.InternalServerError,
          message = EmptyResponse(success = false)
        )
      } else {
        call.respond(
          status = HttpStatusCode.OK,
          message = EmptyResponse(success = true)
        )
      }
    }

    patch("{id}") {
      val userModel = call.receive<UserModel>()
      val id = call.parameters["id"]
        ?: return@patch call.respond(
          status = HttpStatusCode.BadRequest,
          message = EmptyResponse(success = false)
        )

      val result = db.patchUserAtID(userModel, id)

      if (result == 0) {
        call.response.status(HttpStatusCode.NotFound)
        call.respond(
          status = HttpStatusCode.NotFound,
          message = EmptyResponse(success = false)
        )
      } else {
        call.respond(
          status = HttpStatusCode.OK,
          message = EmptyResponse(success = true)
        )
      }
    }
  }
}

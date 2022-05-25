package com.example.routes

import com.example.db.DatabaseConnection
import com.example.models.EmptyResponse
import com.example.models.UserModel
import com.example.models.UserRequest
import com.example.models.BaseResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.usersRouting(db: DatabaseConnection) {
  route("api/users") {

    get {
      val users = db.getAllUsers()
      call.respond(
        status = HttpStatusCode.OK,
        message = BaseResponse(success = true, data = users)
      )
    }

    post {
      val request = call.receive<MutableList<UserRequest>>()

      val list = request.map {
        val id = db.generateRandomID()
        UserModel(
          id = id,
          name = it.name,
          password = it.password
        )
      }.toMutableList()

      db.postAll(list)

      call.respond(
        status = HttpStatusCode.OK,
        message = EmptyResponse(success = true)
      )
    }

  }
}

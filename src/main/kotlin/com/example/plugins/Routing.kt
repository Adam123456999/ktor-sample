package com.example.plugins

//import com.example.routes.authRouting
//import com.example.routes.usersRouting
//import com.example.utils.TokenManager
import com.example.db.DatabaseConnection
import com.example.routes.usersRouting
import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import java.io.File

// db: DatabaseConnection, tokenManager: TokenManager
fun Application.configureRouting(db: DatabaseConnection) {

  routing {
    authenticate("auth-basic") {
      get("/auth-basic") {
        call.respondText("Hello, ${call.principal<UserIdPrincipal>()?.name}!")
      }
    }

    get("/") {
      call.respondText("Hello, World!")
    }

    get("/file-download") {
      val file = File("src/main/resources/File1.png")
      call.response.header(HttpHeaders.ContentDisposition,
        ContentDisposition.Attachment.withParameter(
          ContentDisposition.Parameters.FileName, "downloadableImage.png"
        ).toString()
      )
      call.respondFile(file)
    }

    get("/file-open") {
      val file = File("src/main/resources/File2.png")
      call.response.header(HttpHeaders.ContentDisposition,
        ContentDisposition.Inline.withParameter(
          ContentDisposition.Parameters.FileName, "downloadableImage.png"
        ).toString()
      )
      call.respondFile(file)
    }

    static("/static") {
      resources("static")
    }
    usersRouting(db)
//    authRouting(db, tokenManager)
  }
}

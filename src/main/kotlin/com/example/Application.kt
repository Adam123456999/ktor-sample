package com.example

import com.example.db.DatabaseConnection
import com.example.models.UserModel
import com.example.plugins.*
import com.example.utils.TokenManager
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.config.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

  val database = DatabaseConnection
  val tokenManager = TokenManager(HoconApplicationConfig(ConfigFactory.load()))

  configureHTTP()
  configureSerialization()
  configureAuth(tokenManager)
  configureRouting(database, tokenManager)
}
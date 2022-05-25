package com.example

import com.example.db.DatabaseConnection
import com.example.plugins.*
import io.ktor.server.application.*
import java.io.File
//import com.example.utils.TokenManager
//import com.typesafe.config.ConfigFactory

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {

//  val tokenManager = TokenManager(HoconApplicationConfig(ConfigFactory.load()))

  val database = DatabaseConnection

  configureHTTP()
  configureSerialization()
//  configureAuth(tokenManager)
  configureRouting(database)

}
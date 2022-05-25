package com.example.plugins

import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureHTTP() {
  val port = HoconApplicationConfig(ConfigFactory.load()).property("ktor.deployment.port")
  install(CORS) {
    allowMethod(HttpMethod.Delete)
    allowMethod(HttpMethod.Patch)
    allowHeader(HttpHeaders.AccessControlAllowHeaders)
    allowHeader(HttpHeaders.ContentType)
    allowHeader(HttpHeaders.AccessControlAllowOrigin)
    allowHeader(HttpHeaders.Authorization)
    allowHost("glacial-lowlands-26951.herokuapp.com:$port", schemes = listOf("https"))
  }
}

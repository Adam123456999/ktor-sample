package com.example.db

import com.example.models.UserModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object DatabaseConnection {
  private val usersFile = File("src/main/kotlin/com/example/db/Database.json")

  fun getAllUsers(): MutableList<UserModel> = Json.decodeFromString(usersFile.readText())

  fun postAll(list: MutableList<UserModel>) = usersFile.writeText(Json.encodeToString(list))

  fun generateRandomID(): String {
    val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    val users = getAllUsers()
    var id: String
    var duplicate = false

    do {
      id = (1..20)
        .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("");

      users.forEach {
        if (it.id == id) duplicate = true
      }
    } while (duplicate)
    return id
  }
}
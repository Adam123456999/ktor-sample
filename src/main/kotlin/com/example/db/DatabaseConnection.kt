package com.example.db

import com.example.models.UserModel
import com.example.entities.UserEntity
import org.ktorm.database.Database
import org.ktorm.dsl.*

object DatabaseConnection {
  private val db = Database.connect(
    url = "jdbc:mysql://localhost:3306/base_schema",
    driver = "com.mysql.cj.jdbc.Driver",
    user = "root",
    password = "ConDor342"
  )

  fun getAllUsers(): List<UserModel> {
    val users = db.from(UserEntity).select()
      .map { user ->
        val id = user[UserEntity.id]
        val name = user[UserEntity.name]
        val password = user[UserEntity.password]
        UserModel(id, name, password)
      }
    return users
  }

  fun getUserByID(id: String): UserModel? {
    val users = getAllUsers()
    return users.find { it.id == id }
  }

  fun addUser(userModel: UserModel, id: String): Int {
    return db.insert(UserEntity) {
      set(it.id, id)
      set(it.name, userModel.name)
      set(it.password, userModel.password)
    }
  }

  fun addUser(userModel: UserModel): Int {
    return db.insert(UserEntity) {
      set(it.id, userModel.id)
      set(it.name, userModel.name)
      set(it.password, userModel.password)
    }
  }

  fun putUserAtID(userModel: UserModel, id: String): Int {
    var result = db.update(UserEntity) {
      set(it.name, userModel.name)
      set(it.password, userModel.password)
      where { it.id eq id }
    }

    if (result == 0) {
      result = addUser(userModel, id)
    }
    return result
  }

  fun patchUserAtID(userModel: UserModel, id: String): Int {
    return db.update(UserEntity) {
      set(it.name, userModel.name)
      set(it.password, userModel.password)
      where { it.id eq id }
    }
  }

  fun deleteAllUsers(): Int = db.deleteAll(UserEntity)

  fun deleteUserAtID(id: String): Int = db.delete(UserEntity) {it.id eq id}

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
package com.example.entities

import org.ktorm.schema.Table
import org.ktorm.schema.varchar

object UserEntity : Table<Nothing>("users") {
  val id = varchar("id").primaryKey()
  var name = varchar("name")
  var password = varchar("password")
}



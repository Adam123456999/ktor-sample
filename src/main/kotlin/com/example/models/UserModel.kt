package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
  val id: String?,
  val name: String?,
  val password: String?,
)

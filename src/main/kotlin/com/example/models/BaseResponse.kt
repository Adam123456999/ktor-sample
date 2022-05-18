package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class BaseResponse<T>(
  val success: Boolean,
  val data: T,
)

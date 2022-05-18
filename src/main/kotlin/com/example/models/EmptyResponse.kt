package com.example.models

import kotlinx.serialization.Serializable

@Serializable
data class EmptyResponse(
  val success: Boolean,
  val data: EmptyResponse ?= null,
)
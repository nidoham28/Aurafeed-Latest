package com.nidoham.aurafeed.core.navigation

import kotlinx.serialization.Serializable

sealed interface AurafeedRoute

@Serializable
data object Splash : AurafeedRoute

@Serializable
data object Auth : AurafeedRoute

@Serializable
data object Shell : AurafeedRoute
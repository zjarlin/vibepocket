package site.addzero.vibepocket.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class RouteKey(val key: String) : NavKey

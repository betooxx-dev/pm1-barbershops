package com.example.moviles01.viewmodel.shared

sealed class Screen {
    data object Login : Screen()
    data object Register : Screen()
    data object BarbershopList : Screen()
    data object BarbershopForm : Screen()
}

data class SharedState(
    val currentScreen: Screen = Screen.Login,
    val isAuthenticated: Boolean = false,
    val previousScreen: Screen? = null
)
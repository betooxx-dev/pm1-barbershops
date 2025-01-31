package com.example.moviles01.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moviles01.view.screens.auth.LoginScreen
import com.example.moviles01.view.screens.auth.RegisterScreen
import com.example.moviles01.view.screens.barbershop.BarbershopScreen
import com.example.moviles01.view.screens.barbershop.BarbershopForm

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object BarbershopList : Screen("barbershops")
    object BarbershopForm : Screen("barbershop_form")
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.BarbershopList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.BarbershopList.route) {
            BarbershopScreen(
                onAddClick = {
                    navController.navigate(Screen.BarbershopForm.route)
                },
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.BarbershopForm.route) {
            BarbershopForm(
                onCancel = {
                    navController.navigateUp()
                },
                onSuccess = {
                    navController.navigateUp()
                }
            )
        }
    }
}
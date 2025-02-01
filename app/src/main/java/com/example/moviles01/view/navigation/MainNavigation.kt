package com.example.moviles01.view.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.moviles01.view.screens.auth.LoginScreen
import com.example.moviles01.view.screens.auth.RegisterScreen
import com.example.moviles01.view.screens.barbershop.BarbershopScreen
import com.example.moviles01.view.screens.barbershop.BarbershopForm
import com.example.moviles01.viewmodel.auth.AuthViewModel
import com.example.moviles01.viewmodel.barbershop.BarbershopViewModel
import com.example.moviles01.viewmodel.shared.SharedViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object BarbershopList : Screen("barbershops")
    object BarbershopForm : Screen("barbershop_form")
}

@Composable
fun MainNavigation(
    authViewModel: AuthViewModel,
    barbershopViewModel: BarbershopViewModel,
    sharedViewModel: SharedViewModel
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()
    val barbershopState by barbershopViewModel.state.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
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
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.BarbershopList.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.BarbershopList.route) {
            BarbershopScreen(
                barbershops = barbershopState.barbershops,
                isLoading = barbershopState.isLoading,
                onAddClick = {
                    barbershopViewModel.selectBarbershop(null)
                    navController.navigate(Screen.BarbershopForm.route)
                },
                onEditClick = { barbershop ->
                    barbershopViewModel.selectBarbershop(barbershop)
                    navController.navigate(Screen.BarbershopForm.route)
                },
                onDeleteClick = { id ->
                    barbershopViewModel.showDeleteConfirmation(id)
                },
                onLogoutClick = {
                    authViewModel.logout()
                    sharedViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.BarbershopForm.route) {
            BarbershopForm(
                barbershop = barbershopState.selectedBarbershop,
                isLoading = barbershopState.isLoading,
                onSubmit = { barbershop ->
                    if (barbershopState.selectedBarbershop != null) {
                        barbershopViewModel.updateBarbershop(barbershop)
                    } else {
                        barbershopViewModel.createBarbershop(barbershop)
                    }
                    navController.navigateUp()
                },
                onCancel = {
                    barbershopViewModel.selectBarbershop(null)
                    navController.navigateUp()
                }
            )
        }
    }
}
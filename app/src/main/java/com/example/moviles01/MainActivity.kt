package com.example.moviles01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import com.example.moviles01.model.data.Barbershop
import com.example.moviles01.model.network.ApiService
import com.example.moviles01.model.network.AuthInterceptor
import com.example.moviles01.model.repository.AuthRepository
import com.example.moviles01.model.repository.BarbershopRepository
import com.example.moviles01.model.repository.SharedPreferencesManager
import com.example.moviles01.ui.theme.Moviles01Theme
import com.example.moviles01.view.components.ErrorDialog
import com.example.moviles01.view.screens.auth.LoginScreen
import com.example.moviles01.view.screens.auth.RegisterScreen
import com.example.moviles01.view.screens.barbershop.BarbershopForm
import com.example.moviles01.view.screens.barbershop.BarbershopScreen
import com.example.moviles01.viewmodel.auth.AuthViewModel
import com.example.moviles01.viewmodel.auth.AuthViewModelFactory
import com.example.moviles01.viewmodel.barbershop.BarbershopViewModel
import com.example.moviles01.viewmodel.barbershop.BarbershopViewModelFactory
import com.example.moviles01.viewmodel.shared.SharedViewModel
import com.example.moviles01.viewmodel.shared.SharedViewModelFactory
import com.example.moviles01.viewmodel.shared.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    private lateinit var apiService: ApiService
    private lateinit var prefsManager: SharedPreferencesManager
    private lateinit var authViewModel: AuthViewModel
    private lateinit var barbershopViewModel: BarbershopViewModel
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDependencies()
        setupViewModels()

        setContent {
            Moviles01Theme {
                MainScreen()
            }
        }
    }

    private fun setupDependencies() {
        prefsManager = SharedPreferencesManager(this)

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(prefsManager))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        apiService = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private fun setupViewModels() {
        val authRepository = AuthRepository(apiService, prefsManager)
        val barbershopRepository = BarbershopRepository(apiService)

        authViewModel = ViewModelProvider(
            this,
            AuthViewModelFactory(authRepository)
        )[AuthViewModel::class.java]

        barbershopViewModel = ViewModelProvider(
            this,
            BarbershopViewModelFactory(barbershopRepository)
        )[BarbershopViewModel::class.java]

        sharedViewModel = ViewModelProvider(
            this,
            SharedViewModelFactory(prefsManager)
        )[SharedViewModel::class.java]
    }

    @Composable
    private fun MainScreen() {
        val sharedState by sharedViewModel.state.collectAsState()
        val authState by authViewModel.authState.collectAsState()
        val barbershopState by barbershopViewModel.state.collectAsState()

        LaunchedEffect(authState.isAuthenticated) {
            if (authState.isAuthenticated) {
                sharedViewModel.setAuthenticated(true)
                barbershopViewModel.loadBarbershops()
            }
        }

        LaunchedEffect(authState.error, barbershopState.error) {
            authState.error?.let { error ->
                authViewModel.clearError()
            }
            barbershopState.error?.let { error ->
                barbershopViewModel.clearError()
            }
        }

        when (sharedState.currentScreen) {
            is Screen.Login -> {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegister = {
                        sharedViewModel.navigate(Screen.Register)
                    },
                    onLoginSuccess = {
                        sharedViewModel.navigate(Screen.BarbershopList)
                    }
                )
            }
            is Screen.Register -> {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateToLogin = {
                        sharedViewModel.navigate(Screen.Login)
                    },
                    onRegisterSuccess = {
                        sharedViewModel.navigate(Screen.BarbershopList)
                    }
                )
            }
            is Screen.BarbershopList -> {
                BarbershopScreen(
                    barbershops = barbershopState.barbershops,
                    isLoading = barbershopState.isLoading,
                    onAddClick = {
                        barbershopViewModel.selectBarbershop(null)
                        sharedViewModel.navigate(Screen.BarbershopForm)
                    },
                    onEditClick = { barbershop ->
                        barbershopViewModel.selectBarbershop(barbershop)
                        sharedViewModel.navigate(Screen.BarbershopForm)
                    },
                    onDeleteClick = { id ->
                        barbershopViewModel.showDeleteConfirmation(id)
                    },
                    onLogoutClick = {
                        authViewModel.logout()
                        sharedViewModel.logout()
                    }
                )

                if (barbershopState.isDeleteDialogVisible) {
                    DeleteConfirmationDialog(
                        onConfirm = {
                            barbershopState.barbershopToDelete?.let { id ->
                                barbershopViewModel.deleteBarbershop(id)
                            }
                            barbershopViewModel.hideDeleteConfirmation()
                        },
                        onDismiss = {
                            barbershopViewModel.hideDeleteConfirmation()
                        }
                    )
                }
            }
            is Screen.BarbershopForm -> {
                val context = LocalContext.current  // Agregamos esta línea
                BarbershopForm(
                    barbershop = barbershopState.selectedBarbershop,
                    isLoading = barbershopState.isLoading,
                    onSubmit = { barbershop ->
                        if (barbershopState.selectedBarbershop != null) {
                            barbershopViewModel.updateBarbershop(barbershop)
                        } else {
                            barbershopViewModel.createBarbershop(barbershop)
                        }
                        sharedViewModel.navigate(Screen.BarbershopList)
                    },
                    onCancel = {
                        barbershopViewModel.selectBarbershop(null)
                        sharedViewModel.navigateBack()
                    },
                    onUploadImage = { uri ->
                        withContext(Dispatchers.IO) {
                            barbershopViewModel.uploadImage(uri, context)
                        }
                    }
                )
            }
        }

        if (authState.error != null || barbershopState.error != null) {
            ErrorDialog(
                errorMessage = authState.error ?: barbershopState.error ?: "Error desconocido",
                onDismiss = {
                    authViewModel.clearError()
                    barbershopViewModel.clearError()
                }
            )
        }
    }

    @Composable
    private fun DeleteConfirmationDialog(
        onConfirm: () -> Unit,
        onDismiss: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar esta barbería?") },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancelar")
                }
            }
        )
    }
}
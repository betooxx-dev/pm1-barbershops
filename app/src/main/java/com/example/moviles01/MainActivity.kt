package com.example.moviles01

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
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
import com.google.firebase.messaging.FirebaseMessaging
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            setupFirebaseMessaging()
        } else {
            // nada
        }
    }



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupDependencies()
        setupViewModels()
        setupFirebaseMessaging()
        askNotificationPermission()

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

    private fun setupFirebaseMessaging() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                // Guardar el token
                prefsManager.saveFcmToken(token)
                // Imprimir en el log
                Log.d("FCM_TOKEN_MAIN", "Token obtenido: $token")

                // Mostrar el token en un Toast (versión corta para visualización)
                val shortToken = if (token.length > 20) "${token.substring(0, 20)}..." else token
                Toast.makeText(
                    this,
                    "Token FCM: $shortToken",
                    Toast.LENGTH_LONG
                ).show()

                // También puedes mostrar un Toast más informativo
                Handler(Looper.getMainLooper()).postDelayed({
                    Toast.makeText(
                        this,
                        "Token generado correctamente. Revisa los logs para el token completo.",
                        Toast.LENGTH_LONG
                    ).show()
                }, 3500) // Mostrar después de 3.5 segundos
            } else {
                // Si hay error, registrarlo y mostrar Toast de error
                Log.e("FCM_TOKEN_ERROR", "Error al obtener token FCM", task.exception)
                Toast.makeText(
                    this,
                    "Error al obtener token FCM: ${task.exception?.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun askNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) !=
            android.content.pm.PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
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
                val context = LocalContext.current
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
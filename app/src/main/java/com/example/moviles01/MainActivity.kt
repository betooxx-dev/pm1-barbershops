package com.example.moviles01

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.lifecycle.lifecycleScope
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.moviles01.ui.theme.Moviles01Theme
import kotlinx.coroutines.launch
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {
    private lateinit var apiService: ApiService
    private lateinit var prefsManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefsManager = SharedPreferencesManager(this)

        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(prefsManager))
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .cookieJar(object : CookieJar {
                private val cookieStore = HashMap<String, List<Cookie>>()

                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookieStore[url.host] = cookies
                }

                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    return cookieStore[url.host] ?: ArrayList()
                }
            })
            .build()

        apiService = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        setContent {
            Moviles01Theme {
                var currentScreen by remember { mutableStateOf("login") }
                var userData by remember { mutableStateOf<UserData?>(null) }
                var barbershops by remember { mutableStateOf<List<Barbershop>>(emptyList()) }
                var showDeleteConfirmationDialog by remember { mutableStateOf(false) }
                var barbershopToDelete by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(currentScreen) {
                    if (currentScreen == "barbershops") {
                        fetchBarbershops { fetchedBarbershops ->
                            barbershops = fetchedBarbershops
                        }
                    }
                }

                // Diálogo de confirmación de eliminación
                if (showDeleteConfirmationDialog) {
                    AlertDialog(
                        onDismissRequest = {
                            showDeleteConfirmationDialog = false
                            barbershopToDelete = null
                        },
                        title = { Text("Confirmar eliminación") },
                        text = { Text("¿Estás seguro de que deseas eliminar esta barbería?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    barbershopToDelete?.let { id ->
                                        handleDeleteBarbershop(id)
                                    }
                                    showDeleteConfirmationDialog = false
                                    barbershopToDelete = null
                                }
                            ) {
                                Text("Eliminar")
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = {
                                    showDeleteConfirmationDialog = false
                                    barbershopToDelete = null
                                }
                            ) {
                                Text("Cancelar")
                            }
                        }
                    )
                }

                when (currentScreen) {
                    "login" -> LoginScreen(
                        onLoginClick = { email, password ->
                            handleLogin(email, password) { user ->
                                userData = user
                                currentScreen = "barbershops"
                            }
                        },
                        onNavigateToRegister = { currentScreen = "register" }
                    )
                    "register" -> RegisterScreen(
                        onRegisterClick = { firstName, lastName, email, password ->
                            handleRegister(firstName, lastName, email, password) { user ->
                                userData = user
                                currentScreen = "login"
                            }
                        },
                        onNavigateToLogin = { currentScreen = "login" }
                    )
                    "barbershops" -> BarbershopsScreen(
                        barbershops = barbershops,
                        onAddClick = { currentScreen = "barbershop_form" },
                        onEditClick = { barbershop ->
                            selectedBarbershop = barbershop
                            currentScreen = "barbershop_form"
                        },
                        onDeleteClick = { id ->
                            showDeleteConfirmationDialog = true
                            barbershopToDelete = id
                        },
                        onLogoutClick = { handleLogout() }
                    )
                    "barbershop_form" -> BarbershopForm(
                        barbershop = selectedBarbershop,
                        onSubmit = { barbershop ->
                            if (selectedBarbershop != null) {
                                handleUpdateBarbershop(barbershop)
                            } else {
                                handleCreateBarbershop(barbershop)
                            }
                            selectedBarbershop = null
                            currentScreen = "barbershops"
                        },
                        onCancel = {
                            selectedBarbershop = null
                            currentScreen = "barbershops"
                        }
                    )
                }
            }
        }
    }

    private var selectedBarbershop: Barbershop? = null

    private fun fetchBarbershops(onSuccess: (List<Barbershop>) -> Unit) {
        lifecycleScope.launch {
            try {
                val response = apiService.getBarbershops()
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Error al cargar las barberías",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleCreateBarbershop(barbershop: Barbershop) {
        lifecycleScope.launch {
            try {
                val response = apiService.createBarbershop(barbershop)
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@MainActivity,
                        "Barbería creada exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    fetchBarbershops { /* actualizar lista */ }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Error al crear la barbería",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleUpdateBarbershop(barbershop: Barbershop) {
        lifecycleScope.launch {
            try {
                val response = apiService.updateBarbershop(barbershop.id, barbershop)
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@MainActivity,
                        "Barbería actualizada exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    fetchBarbershops { /* actualizar lista */ }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Error al actualizar la barbería",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleDeleteBarbershop(id: String) {
        lifecycleScope.launch {
            try {
                val response = apiService.deleteBarbershop(id)
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@MainActivity,
                        "Barbería eliminada exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    fetchBarbershops { /* actualizar lista */ }
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Error al eliminar la barbería",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleLogin(email: String, password: String, onSuccess: (UserData) -> Unit) {
        lifecycleScope.launch {
            try {
                val response = apiService.login(mapOf(
                    "email" to email.trim(),
                    "password" to password.trim()
                ))

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    // Guardar el token
                    val token = loginResponse.data.token
                    prefsManager.saveToken(token)

                    // Verificar si tenemos el token
                    Log.d("LoginDebug", "Token guardado: ${prefsManager.getToken()}")

                    onSuccess(loginResponse.data.user)
                    runOnUiThread {
                        Toast.makeText(this@MainActivity, loginResponse.message, Toast.LENGTH_LONG).show()
                    }
                } else {
                    runOnUiThread {
                        Toast.makeText(
                            this@MainActivity,
                            "Error de autenticación: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("LoginError", "Exception: ${e.message}")
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        "Error de conexión: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun handleRegister(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        onSuccess: (UserData) -> Unit
    ) {
        lifecycleScope.launch {
            try {
                val response = apiService.register(mapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "email" to email,
                    "password" to password
                ))

                if (response.isSuccessful && response.body() != null) {
                    val registerResponse = response.body()!!
                    prefsManager.saveToken(registerResponse.data.token)
                    onSuccess(registerResponse.data.user)
                    Toast.makeText(
                        this@MainActivity,
                        registerResponse.message,
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        "Error en el registro",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Error de conexión: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleLogout() {
        prefsManager.clearToken()
        selectedBarbershop = null
        setContent {
            Moviles01Theme {
                LoginScreen(
                    onLoginClick = { email, password ->
                        handleLogin(email, password) { user ->
                            setContent {
                                Moviles01Theme {
                                    BarbershopsScreen(
                                        barbershops = emptyList(),
                                        onAddClick = { /* ... */ },
                                        onEditClick = { /* ... */ },
                                        onDeleteClick = { /* ... */ },
                                        onLogoutClick = { handleLogout() }
                                    )
                                }
                            }
                        }
                    },
                    onNavigateToRegister = { /* ... */ }
                )
            }
        }
    }
}
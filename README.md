# 💈 Barbershop App 💈

## 📱 Descripción

Esta aplicación móvil para Android permite a los usuarios gestionar barberías. Desarrollada con Kotlin y Jetpack Compose.

## 🛠️ Características principales

- 🔐 Sistema de autenticación (registro e inicio de sesión)
- 📋 Listado de barberías
- ✏️ Creación, edición y eliminación de barberías
- 📷 Carga de imágenes
- 🔔 Sistema de notificaciones push con Firebase

## 🏗️ Arquitectura

El proyecto sigue el patrón MVVM (Model-View-ViewModel) con una clara separación de responsabilidades:

- **Model**: Clases de datos y repositorios
- **View**: Pantallas y componentes en Jetpack Compose
- **ViewModel**: Lógica de negocio y estado de la UI

### 📦 Estructura del proyecto

```
com.example.moviles01/
├── model/
│   ├── data/           # Clases de datos (Barbershop, UserData, etc.)
│   ├── network/        # Configuración de red (ApiService, AuthInterceptor)
│   ├── notification/   # Manejo de notificaciones Firebase
│   └── repository/     # Repositorios para acceso a datos
├── ui/
│   └── theme/          # Estilos y temas de la aplicación
├── view/
│   ├── components/     # Componentes UI reutilizables
│   ├── navigation/     # Configuración de navegación
│   └── screens/        # Pantallas de la aplicación
└── viewmodel/          # ViewModels para cada sección
    ├── auth/           # Autenticación
    ├── barbershop/     # Gestión de barberías
    ├── notification/   # Notificaciones
    └── shared/         # Estado compartido
```

## 🔧 Tecnologías utilizadas

- **Kotlin**: Lenguaje principal
- **Jetpack Compose**: Framework moderno para UI
- **Coroutines**: Programación asíncrona
- **Retrofit**: Cliente HTTP para comunicación con API
- **Firebase Cloud Messaging**: Para notificaciones push
- **SharedPreferences**: Almacenamiento local seguro
- **ViewModel y StateFlow**: Gestión de estado reactivo

## 🚀 Mejoras implementadas

### 🔒 Seguridad mejorada

Se ha implementado `EncryptedSharedPreferences` para almacenar de forma segura los tokens de autenticación y otros datos sensibles.

```kotlin
private val prefs = EncryptedSharedPreferences.create(
    context,
    "SecureAppPrefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
)
```

### 🧠 Detección de fugas de memoria

Se utiliza LeakCanary para detectar y prevenir fugas de memoria durante el desarrollo.

```gradle
debugImplementation 'com.squareup.leakcanary:leakcanary-android:2.10'
```

## 📋 Permisos requeridos

- `INTERNET`: Para comunicación con el servidor
- `CAMERA`: Para tomar fotos de barberías
- `READ_EXTERNAL_STORAGE`: Acceso a imágenes existentes (Android < 13)
- `READ_MEDIA_IMAGES`: Acceso a imágenes (Android 13+)
- `POST_NOTIFICATIONS`: Para mostrar notificaciones push

## 💻 API y Backend

La aplicación se comunica con un servidor backend a través de una API RESTful. Las principales rutas utilizadas son:

- `/auth/login` - Autenticación de usuarios
- `/auth/register` - Registro de nuevos usuarios
- `/barbershop` - CRUD de barberías
- `/barbershop/upload` - Carga de imágenes

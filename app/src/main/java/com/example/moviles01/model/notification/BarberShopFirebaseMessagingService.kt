package com.example.moviles01.model.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.moviles01.MainActivity
import com.example.moviles01.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class BarberShopFirebaseMessagingService1 : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.data.isNotEmpty().let {
            handleNow(remoteMessage)
        }

        remoteMessage.notification?.let {
            sendNotification(it.title, it.body)
        }
    }

    override fun onNewToken(token: String) {
        sendRegistrationToServer(token)
    }

    private fun handleNow(remoteMessage: RemoteMessage) {
        val barbershopId = remoteMessage.data["barbershopId"]
        val actionType = remoteMessage.data["actionType"]

        when (actionType) {
            "new_appointment" -> sendNotification(
                "Nueva Cita",
                "Has recibido una nueva solicitud de cita"
            )
            "appointment_update" -> sendNotification(
                "Actualización de Cita",
                "Una cita ha sido actualizada"
            )
            "appointment_cancel" -> sendNotification(
                "Cancelación de Cita",
                "Una cita ha sido cancelada"
            )
            else -> remoteMessage.notification?.let {
                sendNotification(it.title, it.body)
            }
        }
    }

    private fun sendNotification(title: String?, messageBody: String?) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "fcm_default_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title ?: "Barber Shop")
            .setContentText(messageBody ?: "Tienes una nueva notificación")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Canal de Notificaciones de Barbershop",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }

    private fun sendRegistrationToServer(token: String) {
        // Esto es importante para enviar notificaciones específicas a este dispositivo
    }
}
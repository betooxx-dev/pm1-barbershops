package com.example.moviles01.model.data

data class Service(
    val name: String,
    val price: Double
)

data class Location(
    val city: String,
    val street: String
)

data class WorkingDays(
    val days: String,
    val schedule: String
)

data class Contact(
    val phone: String,
    val email: String
)

data class Photo(
    val imageURL: String
)

data class Barbershop(
    val id: String = "",
    val name: String,
    val description: String,
    val location: Location,
    val services: List<Service>,
    val workingDays: WorkingDays,
    val contact: Contact,
    val logo: String,
    val photos: List<Photo>,
    val owner: String,
    val appointments: List<String> = listOf(),
    val reviews: List<String> = listOf(),
    val payments: List<String> = listOf()
)
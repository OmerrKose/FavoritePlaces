package com.example.favoriteplaces.models

/**
 * This is the data class created to store the user inputs into the application
 */
data class FavoritePlaceModel(
    val id: Int,
    val title: String,
    val image: String,
    val description: String,
    val date: String,
    val location: String,
    val latitude: Double,
    val longitude: Double
)
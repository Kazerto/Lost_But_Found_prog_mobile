package com.example.myapplicationlbf.Request


//title, description, latitude, longitude, reported_by
data class LostItemRequest(
    val title: String,
    val description: String,
    val ville: String,
    val latitude: String,
    val longitude: String,
    val imageUrl: String? // En base64
)
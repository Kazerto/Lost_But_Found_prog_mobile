package com.example.myapplicationlbf.Request

// Requête d'inscription avec username, email et mot de passe
data class RegisterRequest(
    val username : String,
    val email: String,
    val phone : String,
    val password: String
)

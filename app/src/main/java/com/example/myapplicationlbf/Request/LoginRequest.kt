package com.example.myapplicationlbf.Request

// Requête de connexion avec email et mot de passe
data class LoginRequest(
    val email: String,
    val password: String
)

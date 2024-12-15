package com.example.myapplicationlbf.Response

// Réponse de l'API lors de la connexion
data class LoginResponse(
    val status: String,   // Exemple : "success" ou "error"
    val message: String?,  // Message d'erreur ou de succès (facultatif)
    val token: String
)

package com.example.myapplicationlbf

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LauncherActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Vérifier si l'utilisateur est connecté
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken.isNullOrEmpty()) {
            // Rediriger vers HomeActivity si l'utilisateur n'est pas connecté
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            // Rediriger vers MainActivity si l'utilisateur est connecté
            startActivity(Intent(this, MainActivity::class.java))
        }
        finish() // Fermer LauncherActivity
    }
}

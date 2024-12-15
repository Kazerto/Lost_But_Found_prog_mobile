package com.example.myapplicationlbf

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapplicationlbf.Interface.ApiService
import com.example.myapplicationlbf.Response.User
import com.example.myapplicationlbf.databinding.ActivityUserProfileBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Response

class UserProfileActivity : AppCompatActivity() {

    // Initialisation du binding pour l'interface utilisateur
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation du binding
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialiser Retrofit et ApiService
        val retrofit = Retrofit.Builder()
            .baseUrl("https://lbf-ing3-2024.onrender.com/") // Mettez votre URL de base ici
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Récupérer le token d'authentification depuis SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken != null) {
            // Appeler la méthode pour obtenir les informations de l'utilisateur
            getUserProfile(authToken)
        } else {
            Toast.makeText(this, "Token d'authentification invalide", Toast.LENGTH_SHORT).show()
        }

        // Lorsque l'utilisateur clique sur "Mettre à jour"
        binding.saveButton.setOnClickListener {
            if (authToken != null) {
                // Appel API pour mettre à jour le profil
                updateUserProfile(authToken)
            }
        }
    }

    // Fonction pour valider l'email avec regex
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(Regex(emailRegex))
    }

    private fun getUserProfile(authToken: String) {
        // Requête pour obtenir les données de l'utilisateur
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getUserProfile("Bearer $authToken")

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user != null) {
                            // Remplir les champs avec les informations mises à jour de l'utilisateur
                            binding.usernameEditText.setText(user.username)
                            binding.emailEditText.setText(user.email)
                            binding.phoneEditText.setText(user.phone)
                            binding.cityEditText.setText(user.city)
                        } else {
                            Toast.makeText(this@UserProfileActivity, "Utilisateur non trouvé", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@UserProfileActivity, "Erreur serveur: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UserProfileActivity, "Erreur réseau : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updateUserProfile(authToken: String) {
        // Récupérer les données des EditText
        val updatedUser = User(
            username = binding.usernameEditText.text.toString(),
            email = binding.emailEditText.text.toString(),
            phone = binding.phoneEditText.text.toString(),
            city = binding.cityEditText.text.toString()
        )

        // Valider l'email avec regex
        if (!isValidEmail(updatedUser.email)) {
            Toast.makeText(this, "L'email saisi est invalide", Toast.LENGTH_SHORT).show()
            return  // Ne pas continuer si l'email est invalide
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Effectuer la requête pour mettre à jour le profil de l'utilisateur
                val response: Response<User> = apiService.updateUserProfile("Bearer $authToken", updatedUser)

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val updatedUser = response.body()
                        if (updatedUser != null) {
                            // Mettre à jour l'interface avec les nouvelles informations
                            binding.usernameEditText.setText(updatedUser.username)
                            binding.emailEditText.setText(updatedUser.email)
                            binding.phoneEditText.setText(updatedUser.phone)
                            binding.cityEditText.setText(updatedUser.city)

                            Toast.makeText(this@UserProfileActivity, "Profil mis à jour avec succès!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@UserProfileActivity, "Erreur lors de la mise à jour", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@UserProfileActivity, "Erreur réseau : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

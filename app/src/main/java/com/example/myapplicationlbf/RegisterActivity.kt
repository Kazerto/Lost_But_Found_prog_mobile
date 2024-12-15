package com.example.myapplicationlbf

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationlbf.databinding.ActivityRegisterBinding
import com.example.myapplicationlbf.Interface.ApiService
import com.example.myapplicationlbf.Request.RegisterRequest
import com.example.myapplicationlbf.Response.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.content.Intent
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Logging Interceptor
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        // OkHttpClient
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        // Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("https://lbf-ing3-2024.onrender.com")  // Use 10.0.2.2 for Android Emulator
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // Gérer le clic sur le lien "Login"
        binding.loginText.setOnClickListener {
            // Créer un Intent pour aller à LoginActivity
            val intent = Intent(this, LoginActivity::class.java)

            // Démarrer l'activité
            startActivity(intent)
        }



        // Gérer le clic sur le bouton d'inscription
        binding.registerButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val phone = binding.phoneEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // Valider l'email avec regex
            if (!isValidEmail(email)) {
                Toast.makeText(this, "L'email saisi est invalide", Toast.LENGTH_SHORT).show()
                return@setOnClickListener  // Ne pas continuer si l'email est invalide
            }


            // Vérifier si les champs sont remplis
            if (username.isBlank() || email.isBlank() || password.isBlank() || phone.isBlank()) {
                Toast.makeText(this@RegisterActivity, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Vérifier si le mot de passe a une longueur minimale de 6 caractères
            if (password.length < 6) {
                Toast.makeText(this@RegisterActivity, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Créer l'objet RegisterRequest avec les informations de l'utilisateur
            val registerRequest = RegisterRequest(username, email, password, phone)

            // Faire la requête d'inscription via l'API
            apiService.registerUser(registerRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        val registerResponse = response.body()
                        if (registerResponse != null && registerResponse?.status == "success") {
                            // Afficher un message de succès
                            Toast.makeText(this@RegisterActivity, "Inscription réussie", Toast.LENGTH_SHORT).show()

                            // Rediriger l'utilisateur vers la page de connexion
                            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish() // Fermer l'écran d'inscription après la réussite
                        } else {
                            // Afficher un message d'erreur spécifique retourné par le serveur
                            Toast.makeText(this@RegisterActivity, registerResponse?.message ?: "Échec de l'inscription", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // En cas d'erreur de réponse (code HTTP non 2xx)
                        val errorMessage = try {
                            // Essayer d'extraire le message d'erreur du corps de la réponse
                            val errorBody = response.errorBody()?.string()
                            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                            errorResponse.message // Récupérer le message d'erreur depuis la réponse
                        } catch (e: Exception) {
                            // Si l'extraction échoue, afficher un message générique
                            "Error connecting to server"
                        }

                        // Afficher le message d'erreur
                        Toast.makeText(this@RegisterActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Afficher un message d'erreur réseau
                    Toast.makeText(this@RegisterActivity, "Erreur réseau : ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
    // Fonction pour valider l'email avec regex
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        return email.matches(Regex(emailRegex))
    }
}

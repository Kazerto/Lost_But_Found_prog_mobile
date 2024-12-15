package com.example.myapplicationlbf

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationlbf.databinding.ActivityLoginBinding
import com.example.myapplicationlbf.Interface.ApiService
import com.example.myapplicationlbf.Request.LoginRequest
import com.example.myapplicationlbf.Response.LoginResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup the logging interceptor for Retrofit
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        // OkHttpClient with the logging interceptor
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)  // Add logging to the OkHttp client
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        // Retrofit instance setup
        val retrofit = Retrofit.Builder()
            .baseUrl("https://lbf-ing3-2024.onrender.com/")  // Replace with your server URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create ApiService instance
        val apiService = retrofit.create(ApiService::class.java)

        // Handle the sign-up link click
        binding.signUpText.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }



        // Handle the login button click
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // Check if email and password are not empty
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this@LoginActivity, "Veuillez entrer votre email et mot de passe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create the login request
            val loginRequest = LoginRequest(email, password)

            // Afficher le ProgressBar
           // binding.loadingProgressBar.visibility = android.view.View.VISIBLE

            // Call the login API
            apiService.loginUser(loginRequest).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    // Masquer le ProgressBar dès qu'on a la réponse
                   // binding.loadingProgressBar.visibility = android.view.View.GONE

                    if (response.isSuccessful) {
                        val loginResponse = response.body()

                        // Vérifier si la réponse contient un statut 'success'
                        if (loginResponse != null && loginResponse.status == "success") {
                            val token = loginResponse.token

                            // Sauvegarder le token dans SharedPreferences
                            val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("auth_token", token)
                            editor.apply()

                            // Naviguer vers MainActivity
                            val intent = Intent(this@LoginActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()  // Fermer la LoginActivity
                        } else {
                            // Afficher un message d'erreur si le statut est différent de 'success'
                            val errorMessage = loginResponse?.message ?: "Identifiants invalides"
                            Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
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
                            "Erreur de connexion au serveur"
                        }

                        // Afficher le message d'erreur
                        Toast.makeText(this@LoginActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    // Masquer le ProgressBar en cas de problème
                   // binding.loadingProgressBar.visibility = android.view.View.GONE

                    // Gérer les erreurs de réseau
                    Toast.makeText(this@LoginActivity, "Erreur réseau: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}

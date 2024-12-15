package com.example.myapplicationlbf

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationlbf.databinding.ActivityResetPasswordBinding
import com.example.myapplicationlbf.Interface.ApiService
import com.example.myapplicationlbf.Request.ResetPasswordRequest
import com.example.myapplicationlbf.Response.LoginResponse
import com.example.myapplicationlbf.Response.ResetPasswordResponse
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private var email: String? = null  // Store the email from ForgotPasswordActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        email = intent.getStringExtra("email")  // Get email passed from ForgotPasswordActivity

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

        // Handle the reset password form submission
        binding.resetPasswordButton.setOnClickListener {
            val otp = binding.otpEditText.text.toString()
            val newPassword = binding.newPasswordEditText.text.toString()
            val confirm = binding.confirmPasswordEditText.text.toString()

            // Vérifier si l'OTP, le nouveau mot de passe et la confirmation ne sont pas vides
            if (otp.isBlank() || newPassword.isBlank()) {
                Toast.makeText(this@ResetPasswordActivity, "Veuillez entrer l'OTP et le nouveau mot de passe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Vérifier si le mot de passe a une longueur minimale de 6 caractères
            if (newPassword.length < 6) {
                Toast.makeText(this@ResetPasswordActivity, "Le mot de passe doit contenir au moins 6 caractères", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Vérifier si le nouveau mot de passe et la confirmation sont identiques
            if (confirm != newPassword) {
                Toast.makeText(this@ResetPasswordActivity, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


            // Create the reset password request
            val resetPasswordRequest = ResetPasswordRequest(email, otp, newPassword)

            // Call the reset password API
            apiService.resetPassword(resetPasswordRequest).enqueue(object : Callback<ResetPasswordResponse> {
                override fun onResponse(call: Call<ResetPasswordResponse>, response: Response<ResetPasswordResponse>) {
                    if (response.isSuccessful) {
                        val resetPasswordResponse = response.body()

                        // Check if the response status is success
                        if (resetPasswordResponse != null && resetPasswordResponse.status == "success") {
                            Toast.makeText(this@ResetPasswordActivity, "Réinitialisation du mot de passe réussie", Toast.LENGTH_SHORT).show()

                            // Redirect to LoginActivity after reset
                            val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()  // Close ResetPasswordActivity
                        } else {
                            Toast.makeText(this@ResetPasswordActivity, resetPasswordResponse?.message ?: "Failed to reset password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // En cas d'erreur de réponse (code HTTP non 2xx)
                        val errorMessage = try {
                            // Essayer d'extraire le message d'erreur du corps de la réponse
                            val errorBody = response.errorBody()?.string()
                            val errorResponse = Gson().fromJson(errorBody, ResetPasswordResponse::class.java)
                            errorResponse.message // Récupérer le message d'erreur depuis la réponse
                        } catch (e: Exception) {
                            // Si l'extraction échoue, afficher un message générique
                            "Error connecting to server"
                        }

                        // Afficher le message d'erreur
                        Toast.makeText(this@ResetPasswordActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResetPasswordResponse>, t: Throwable) {
                    Toast.makeText(this@ResetPasswordActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}

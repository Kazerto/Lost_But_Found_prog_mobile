package com.example.myapplicationlbf

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationlbf.databinding.ActivityForgotPasswordBinding
import com.example.myapplicationlbf.Interface.ApiService
import com.example.myapplicationlbf.Request.ForgotPasswordRequest
import com.example.myapplicationlbf.Response.ForgotPasswordResponse
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

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
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

        // Handle the forgot password form submission
        binding.sendOtpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()

            // Check if email is not empty
            if (email.isBlank()) {
                Toast.makeText(this@ForgotPasswordActivity, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create the forgot password request
            val forgotPasswordRequest = ForgotPasswordRequest(email)

            // Call the forgot password API
            apiService.forgotPassword(forgotPasswordRequest).enqueue(object : Callback<ForgotPasswordResponse> {
                override fun onResponse(call: Call<ForgotPasswordResponse>, response: Response<ForgotPasswordResponse>) {
                    if (response.isSuccessful) {
                        val forgotPasswordResponse = response.body()

                        // Check if the response status is success
                        if (forgotPasswordResponse != null && forgotPasswordResponse.status == "success") {
                            Toast.makeText(this@ForgotPasswordActivity, "Un code de vérification a été envoyé à votre email. Veuillez vérifier votre boîte de réception.", Toast.LENGTH_SHORT).show()

                            // Redirect to ResetPasswordActivity
                            val intent = Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java)
                            intent.putExtra("email", email)  // Pass email to the next activity
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@ForgotPasswordActivity, forgotPasswordResponse?.message ?: "Failed to send OTP", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        val errorMessage = try {
                            // Essayer d'extraire le message d'erreur du corps de la réponse
                            val errorBody = response.errorBody()?.string()
                            val errorResponse = Gson().fromJson(errorBody, ForgotPasswordResponse::class.java)
                            errorResponse.message // Récupérer le message d'erreur depuis la réponse
                        } catch (e: Exception) {
                            // Si l'extraction échoue, afficher un message générique
                            "Error connecting to server"
                        }

                        // Afficher le message d'erreur
                        Toast.makeText(this@ForgotPasswordActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ForgotPasswordResponse>, t: Throwable) {
                    Toast.makeText(this@ForgotPasswordActivity, "Network error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}

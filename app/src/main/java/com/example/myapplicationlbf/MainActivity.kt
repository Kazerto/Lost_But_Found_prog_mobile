package com.example.myapplicationlbf

import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplicationlbf.Interface.ApiService
import com.example.myapplicationlbf.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var apiService: ApiService  // Votre service API pour effectuer les appels réseau

    // CoroutineScope pour lancer des appels suspendus
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // Constante pour la demande de permission
    private val REQUEST_CODE_READ_STORAGE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Initialiser l'apiService
        apiService = Retrofit.Builder()
            .baseUrl("https://lbf-ing3-2024.onrender.com/") // Remplacez par l'URL de votre API
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        // Vérifier si l'utilisateur est connecté
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken == null) {
            // Si le token n'existe pas, redirigez vers la LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()  // Empêche de revenir à MainActivity
        } else {
            // Vérifier si le token est toujours valide
            verifyToken(authToken)
        }

        // Actions des boutons
        binding.logoutButton.setOnClickListener { logout() }

        binding.reportFoundButton.setOnClickListener {
            val intent = Intent(this@MainActivity, LostItemActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Signaler un Objet Trouvé", Toast.LENGTH_SHORT).show()
        }

        binding.searchLostButton.setOnClickListener {
            val intent = Intent(this@MainActivity, LostItemRetrievalActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Rechercher un Objet Perdu", Toast.LENGTH_SHORT).show()
        }

        binding.searchLostOwnButton.setOnClickListener {
            val intent = Intent(this@MainActivity, LostItemOwnRetrievalActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Rechercher un Objet Perdu Poster par moi-même", Toast.LENGTH_SHORT).show()
        }

        binding.myAccountButton.setOnClickListener {
            val intent = Intent(this@MainActivity, UserProfileActivity::class.java)
            startActivity(intent)
            Toast.makeText(this, "Accéder à mon compte", Toast.LENGTH_SHORT).show()
        }
    }



    private fun isNetworkAvailable(): Boolean {
        // Vérifier la connectivité réseau de l'appareil
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun verifyToken(authToken: String) {
        // Vérifier si l'appareil est connecté à Internet avant d'effectuer l'appel réseau
        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Pas de connexion réseau. Veuillez vérifier votre connexion.", Toast.LENGTH_SHORT).show()
            return
        }

        // Lancer la coroutine pour effectuer l'appel réseau asynchrone
        coroutineScope.launch {
            try {
                val response = apiService.verifyTokens("Bearer $authToken")

                if (!response.isSuccessful || response.body() == null) {
                    // Si le token est invalide ou expire
                    Toast.makeText(this@MainActivity, "Token invalide ou expiré. Déconnexion...", Toast.LENGTH_SHORT).show()
                    logout()
                }
                // Si la réponse est réussie, le token est valide et vous pouvez continuer
            } catch (e: Exception) {
                // En cas d'erreur réseau (par exemple, problème de connexion)
                Toast.makeText(this@MainActivity, "Erreur réseau: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                logout()
            }
        }
    }

    private fun logout() {
        // Effacer le token JWT des SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("auth_token")  // Supprimer le token
        editor.apply()

        // Rediriger vers l'écran de connexion
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()  // Empêche de revenir à MainActivity
        Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show()
    }

    // Gérer la réponse à la demande de permission
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE_READ_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission accordée pour lire le stockage", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission de stockage refusée", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

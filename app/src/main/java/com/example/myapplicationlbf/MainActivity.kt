package com.example.myapplicationlbf

import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.myapplicationlbf.Interface.ApiService
import com.example.myapplicationlbf.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var apiService: ApiService  // Votre service API pour effectuer les appels réseau

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    // CoroutineScope pour lancer des appels suspendus
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    // Constante pour la demande de permission
    private val REQUEST_CODE_READ_STORAGE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialiser le Drawer Layout
        drawerLayout = binding.drawerLayout
        navigationView = binding.navigationView

        // Configurer la Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_account) // Icône du menu burger
        }

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

        // Configurer le NavigationView
        setupNavigationView()

        // Charger le fragment par défaut (LostItemRetrieval)
        if (savedInstanceState == null) {
            startActivity(Intent(this, LostItemRetrievalActivity::class.java))
        }

    }

    private fun setupNavigationView() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_report_found -> {
                    startActivity(Intent(this, LostItemActivity::class.java))
                }
                R.id.nav_search_lost -> {
                    startActivity(Intent(this, LostItemRetrievalActivity::class.java))
                }
                R.id.nav_search_own -> {
                    startActivity(Intent(this, LostItemOwnRetrievalActivity::class.java))
                }
                R.id.nav_account -> {
                    startActivity(Intent(this, UserProfileActivity::class.java))
                }
                R.id.nav_logout -> {
                    logout()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
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

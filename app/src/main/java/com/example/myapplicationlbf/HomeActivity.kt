package com.example.myapplicationlbf

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplicationlbf.Interface.ApiService
import com.example.myapplicationlbf.databinding.ActivityHomeBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialiser Retrofit et ApiService pour la récupération des objets perdus
        val retrofit = Retrofit.Builder()
            .baseUrl("https://lbf-ing3-2024.onrender.com/")  // Remplacez par l'URL de votre API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Gestion du bouton pour effectuer la recherche
        binding.btnSearch.setOnClickListener {
            val searchQuery = binding.etSearch.text.toString().trim()
            if (searchQuery.isNotEmpty()) {
                // Lorsque l'utilisateur effectue une recherche, envoyez le terme vers la nouvelle activité
                val intent = Intent(this, SearchResultsActivity::class.java).apply {
                    putExtra("SEARCH_QUERY", searchQuery)  // Passez le terme de recherche
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Veuillez entrer un terme de recherche", Toast.LENGTH_SHORT).show()
            }
        }

        // Gestion du bouton pour afficher la liste des objets perdus
        binding.btnViewClaims.setOnClickListener {
            val intent = Intent(this@HomeActivity, ViewClaimsActivity::class.java)
            startActivity(intent)
        }

        // Gestion du bouton pour signaler un objet perdu
        binding.btnReport.setOnClickListener {
            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}

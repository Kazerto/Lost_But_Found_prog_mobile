package com.example.myapplicationlbf

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplicationlbf.Interface.ApiService
import com.example.myapplicationlbf.Response.LostItemResponse
import com.example.myapplicationlbf.databinding.ActivitySearchResultsBinding
import retrofit2.Retrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory

class SearchResultsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchResultsBinding
    private lateinit var apiService: ApiService
    private var searchQuery: String? = null
    private var lostItemsList: List<LostItemResponse>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialiser Retrofit et ApiService pour la récupération des objets perdus
        val retrofit = Retrofit.Builder()
            .baseUrl("https://lbf-ing3-2024.onrender.com/")  // Remplacez par l'URL de votre API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Récupérer le terme de recherche envoyé depuis HomeActivity
        searchQuery = intent.getStringExtra("SEARCH_QUERY")

        // Afficher le terme de recherche dans un TextView pour confirmation
        binding.tvSearchQuery.text = "Recherche pour : $searchQuery"

        // Récupérer les objets correspondant à la recherche
        fetchLostItems(searchQuery ?: "")

        // Gestion du bouton pour refaire une recherche
        binding.btnSearchAgain.setOnClickListener {
            val newSearchQuery = binding.etSearchAgain.text.toString().trim()
            if (newSearchQuery.isNotEmpty()) {
                fetchLostItems(newSearchQuery)
            } else {
                Toast.makeText(this, "Veuillez entrer un terme de recherche", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchLostItems(query: String) {
        apiService.searchLostItems(query).enqueue(object : Callback<List<LostItemResponse>> {
            override fun onResponse(call: Call<List<LostItemResponse>>, response: Response<List<LostItemResponse>>) {
                if (response.isSuccessful) {
                    lostItemsList = response.body()
                    if (!lostItemsList.isNullOrEmpty()) {
                        binding.recyclerViewLostItems.layoutManager = LinearLayoutManager(this@SearchResultsActivity)
                        binding.recyclerViewLostItems.adapter = HomeLostItemsAdapter(lostItemsList ?: emptyList())
                    } else {
                        Toast.makeText(this@SearchResultsActivity, "Aucun objet trouvé", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SearchResultsActivity, "Erreur lors du chargement des objets perdus", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<LostItemResponse>>, t: Throwable) {
                Toast.makeText(this@SearchResultsActivity, "Erreur de connexion", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

package com.example.myapplicationlbf

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplicationlbf.Interface.ApiService
import com.example.myapplicationlbf.Response.LostItemResponse
import com.example.myapplicationlbf.databinding.ActivityViewClaimsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ViewClaimsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewClaimsBinding
    private lateinit var apiService: ApiService
    private var lostItemsList: List<LostItemResponse> = emptyList() // Liste des objets perdus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewClaimsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialisation de Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://lbf-ing3-2024.onrender.com/")  // Remplacez avec l'URL de votre API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Récupérer la liste des objets perdus
        fetchLostItems()

        // Barre de recherche
        binding.etSearchClaims.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {
                val query = charSequence.toString().trim()
                if (query.isNotEmpty()) {
                    searchLostItems(query)
                } else {
                    binding.recyclerViewClaims.adapter = HomeLostItemsAdapter(lostItemsList)
                }
            }

            override fun afterTextChanged(editable: Editable?) {}
        })
    }

    private fun fetchLostItems() {
        apiService.getLostItems().enqueue(object : Callback<List<LostItemResponse>> {
            override fun onResponse(call: Call<List<LostItemResponse>>, response: Response<List<LostItemResponse>>) {
                if (response.isSuccessful) {
                    lostItemsList = response.body() ?: emptyList()
                    // Initialiser le RecyclerView avec la liste des objets perdus
                    binding.recyclerViewClaims.layoutManager = LinearLayoutManager(this@ViewClaimsActivity)
                    binding.recyclerViewClaims.adapter = HomeLostItemsAdapter(lostItemsList)
                }
            }

            override fun onFailure(call: Call<List<LostItemResponse>>, t: Throwable) {
                // Gestion de l'erreur de connexion
            }
        })
    }

    private fun searchLostItems(query: String) {
        // Filtrer la liste des objets en fonction du terme de recherche
        val filteredItems = lostItemsList.filter {
            it.title.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true)
        }

        // Mettre à jour le RecyclerView avec les éléments filtrés
        binding.recyclerViewClaims.adapter = HomeLostItemsAdapter(filteredItems)
    }
}

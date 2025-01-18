package com.example.myapplicationlbf

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplicationlbf.Interface.ApiService
import com.example.myapplicationlbf.Response.LoginResponse
import com.example.myapplicationlbf.Response.LostItem
import com.example.myapplicationlbf.Response.LostItemsResponse
import com.example.myapplicationlbf.databinding.ActivityLostItemRetrievalBinding
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LostItemOwnRetrievalActivity : BaseActivity() {

    private lateinit var binding: ActivityLostItemRetrievalBinding
    private lateinit var apiService: ApiService
    private lateinit var itemAdapter: LostItemsOwnAdapter
    private var itemList = mutableListOf<LostItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostItemRetrievalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configurer RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        itemAdapter = LostItemsOwnAdapter(listOf(), { lostItem ->
            val intent = Intent(this, LostItemDetailsActivity::class.java)
            intent.putExtra("title", lostItem.title)
            intent.putExtra("description", lostItem.description)
            intent.putExtra("ville", lostItem.address)
            intent.putExtra("image", lostItem.imageUrl)
            startActivity(intent)
        }, { item ->
            // Lorsqu'un utilisateur clique sur "Supprimer"
            deleteItem(item)
        })
        binding.recyclerView.adapter = itemAdapter

        // Créer l'instance Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://lbf-ing3-2024.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Récupérer le token d'authentification depuis SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken != null) {
            fetchLostItems(authToken)
        } else {
            Toast.makeText(this, "Token d'authentification invalide", Toast.LENGTH_SHORT).show()
        }

        // Configurer le SearchView pour la recherche d'objets perdus
        setupSearchView()

        // Setup toolbar
        setupToolbar("Mes objets signalés")

// Setup drawer
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        setupDrawer(drawerLayout, navigationView)
    }

    private fun fetchLostItems(authToken: String, searchQuery: String = "") {
        // Afficher le ProgressBar avant de commencer le chargement
        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getLostItemsOwn("Bearer $authToken", searchQuery)

                withContext(Dispatchers.Main) {
                    // Masquer le ProgressBar après avoir reçu la réponse
                    binding.progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        val items = response.body()?.items ?: emptyList()
                        itemList.clear()
                        itemList.addAll(items)
                        itemAdapter.updateItems(itemList)

                        checkIfListIsEmpty(items)
                    } else {
                        Toast.makeText(this@LostItemOwnRetrievalActivity, "Erreur de récupération des objets", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Masquer le ProgressBar en cas d'erreur réseau
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@LostItemOwnRetrievalActivity, "Erreur réseau : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteItem(item: LostItem) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        if (authToken != null) {
            // Afficher le ProgressBar avant la suppression
            binding.progressBar.visibility = View.VISIBLE

            // Envoyer la requête DELETE
            apiService.deleteLostItem("Bearer $authToken", item.id).enqueue(object : Callback<LostItemsResponse> {
                override fun onResponse(call: Call<LostItemsResponse>, response: Response<LostItemsResponse>) {
                    // Masquer le ProgressBar après la réponse
                    binding.progressBar.visibility = View.GONE

                    if (response.isSuccessful) {
                        Toast.makeText(this@LostItemOwnRetrievalActivity, "Objet supprimé avec succès", Toast.LENGTH_SHORT).show()
                        // Mettre à jour la liste après suppression
                        val updatedItems = itemList.filter { it.id != item.id }
                        itemAdapter.updateItems(updatedItems)
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
                        Toast.makeText(this@LostItemOwnRetrievalActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LostItemsResponse>, t: Throwable) {
                    // Masquer le ProgressBar en cas d'échec de la requête
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this@LostItemOwnRetrievalActivity, "Erreur réseau", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "Token d'authentification manquant", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    fetchLostItems(getAuthToken(), it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    fetchLostItems(getAuthToken(), it)
                }
                return true
            }
        })
    }

    private fun getAuthToken(): String {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", "") ?: ""
    }

    private fun checkIfListIsEmpty(items: List<LostItem>) {
        if (items.isEmpty()) {
            binding.emptyMessageTextView.visibility = View.VISIBLE
        } else {
            binding.emptyMessageTextView.visibility = View.GONE
        }
    }
}


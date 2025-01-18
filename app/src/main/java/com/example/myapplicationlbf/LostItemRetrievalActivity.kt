package com.example.myapplicationlbf

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplicationlbf.Interface.ApiService
import com.example.myapplicationlbf.Response.LostItem
import com.example.myapplicationlbf.databinding.ActivityLostItemBinding
import com.example.myapplicationlbf.databinding.ActivityLostItemRetrievalBinding
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LostItemRetrievalActivity : BaseActivity() {

    private lateinit var binding: ActivityLostItemRetrievalBinding
    private lateinit var apiService: ApiService
    private lateinit var itemAdapter: LostItemsAdapter
    private var itemList = mutableListOf<LostItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostItemRetrievalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configure RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        itemAdapter = LostItemsAdapter(listOf()) { lostItem ->
            val intent = Intent(this, LostItemDetailsActivity::class.java)
            intent.putExtra("title", lostItem.title)
            intent.putExtra("description", lostItem.description)
            intent.putExtra("category", lostItem.category)
            intent.putExtra("image", lostItem.imageUrl)
            intent.putExtra("email", lostItem.reportedBy.email)
            startActivity(intent)
        }
        binding.recyclerView.adapter = itemAdapter

        // Créer l'instance Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://lbf-ing3-2024.onrender.com/") // Utilisation de 10.0.2.2 pour l'émulateur Android
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Récupérer le token d'authentification depuis SharedPreferences
        val authToken = getAuthToken()

        if (authToken != null && authToken.isNotEmpty()) {
            // Appeler la méthode pour récupérer les objets perdus
            fetchLostItems(authToken)
        } else {
            Toast.makeText(this, "Token d'authentification invalide", Toast.LENGTH_SHORT).show()
        }

        // Configurer le SearchView pour la recherche d'objets perdus
        setupSearchView()

        // Setup toolbar
        setupToolbar("Objets perdus")

// Setup drawer
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        setupDrawer(drawerLayout, navigationView)
    }

    private fun fetchLostItems(authToken: String, searchQuery: String = "") {
        // Afficher le ProgressBar avant de commencer le chargement
        binding.progressBar.visibility = android.view.View.VISIBLE

        // Appel à l'API pour récupérer les objets perdus
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getLostItems("Bearer $authToken", searchQuery)

                withContext(Dispatchers.Main) {
                    // Masquer le ProgressBar une fois que la réponse est reçue
                    binding.progressBar.visibility = android.view.View.GONE

                    if (response.isSuccessful) {
                        val items = response.body()?.items ?: emptyList()
                        itemList.clear()
                        itemList.addAll(items)
                        itemAdapter.updateItems(itemList)
                        checkIfListIsEmpty(items)
                    } else {
                        Toast.makeText(this@LostItemRetrievalActivity, "Erreur de récupération des objets", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    // Masquer le ProgressBar en cas d'erreur
                    binding.progressBar.visibility = android.view.View.GONE
                    Toast.makeText(this@LostItemRetrievalActivity, "Erreur réseau : ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    val authToken = getAuthToken()
                    if (authToken != null) {
                        fetchLostItems(authToken, it)
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val authToken = getAuthToken()
                    if (authToken != null) {
                        fetchLostItems(authToken, it)
                    }
                }
                return true
            }
        })
    }

    private fun getAuthToken(): String? {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", "")
    }

    private fun checkIfListIsEmpty(items: List<LostItem>) {
        if (items.isEmpty()) {
            binding.emptyMessageTextView.visibility = android.view.View.VISIBLE
        } else {
            binding.emptyMessageTextView.visibility = android.view.View.GONE
        }
    }
}

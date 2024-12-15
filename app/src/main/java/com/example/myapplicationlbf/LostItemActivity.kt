package com.example.myapplicationlbf

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapplicationlbf.Interface.ApiService
import com.example.myapplicationlbf.Response.LoginResponse
import com.example.myapplicationlbf.Response.LostItemResponse
import com.example.myapplicationlbf.databinding.ActivityAddLostItemBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.util.*

class LostItemActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddLostItemBinding
    private var imageUri: Uri? = null  // URI pour l'image sélectionnée
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddLostItemBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialisation du FusedLocationProviderClient pour récupérer la localisation
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Configuration du Spinner pour les catégories
        val categories = listOf("Electronics", "Clothing", "Books", "Jewelry", "Wallets", "Phones", "Bags", "Keys", "ID Cards", "Other")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategories.adapter = spinnerAdapter

        // Gestion du bouton pour uploader une image
        binding.btnUploadImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"  // Choix d'une image
            startActivityForResult(intent, 101)  // Code de requête = 101
        }

        // Gestion du bouton pour obtenir la localisation actuelle
        binding.btnGetLocation.setOnClickListener {
            getCurrentLocation()
        }

        // Gestion du bouton pour soumettre le formulaire (Créer le cas)
        binding.btnCreateCase.setOnClickListener {
            submitForm()
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                val location: Location? = task.result
                if (location != null) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    getAddressFromLocation(latitude, longitude)
                } else {
                    Toast.makeText(this, "Unable to get current location", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0].getAddressLine(0)
                binding.etAddress.setText(address)
            } else {
                Toast.makeText(this, "Unable to fetch address", Toast.LENGTH_SHORT).show()
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Geocoding failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitForm() {
        val title = binding.etName.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val description = binding.etDescription.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val address = binding.etAddress.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val category = binding.spinnerCategories.selectedItem.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val contactNumber = binding.etContactNumber.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        val imagePart = imageUri?.let { uploadImage(it) }

        val retrofit = Retrofit.Builder()
            .baseUrl("https://lbf-ing3-2024.onrender.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)

        // Récupérer le token d'authentification
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val authToken = sharedPreferences.getString("auth_token", null)

        apiService.reportLostItem(title, description, address, category, contactNumber, imagePart, "Bearer $authToken")
            .enqueue(object : Callback<LostItemResponse> {
                override fun onResponse(call: Call<LostItemResponse>, response: Response<LostItemResponse>) {
                    if (response.isSuccessful) {
                        val intent = Intent(this@LostItemActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(this@LostItemActivity, "Item reported successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        val errorMessage = try {
                            val errorBody = response.errorBody()?.string()
                            val errorResponse = Gson().fromJson(errorBody, LoginResponse::class.java)
                            errorResponse.message
                        } catch (e: Exception) {
                            "Erreur de connexion au serveur"
                        }
                        Toast.makeText(this@LostItemActivity, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<LostItemResponse>, t: Throwable) {
                    Toast.makeText(this@LostItemActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun uploadImage(uri: Uri): MultipartBody.Part {
        val file = File(getRealPathFromURI(uri))
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        return MultipartBody.Part.createFormData("image", file.name, requestBody)
    }

    private fun getRealPathFromURI(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(MediaStore.Images.Media.DATA)
        val filePath = cursor?.getString(columnIndex ?: -1)
        cursor?.close()
        return filePath ?: ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 101) {
            val selectedImageUri = data?.data
            binding.imgView.setImageURI(selectedImageUri)
            imageUri = selectedImageUri
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    // Gestion des permissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

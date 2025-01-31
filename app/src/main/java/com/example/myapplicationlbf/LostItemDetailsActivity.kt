package com.example.myapplicationlbf

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplicationlbf.databinding.ActivityLostItemDetailsBinding
import android.net.Uri
import android.widget.Button

class LostItemDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLostItemDetailsBinding
    private lateinit var titleTextView: TextView
    private lateinit var villeTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var locationTextView: TextView
    private lateinit var openMapButton: Button
    private lateinit var imageView: ImageView
    private lateinit var phoneTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLostItemDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Références aux vues
        titleTextView = binding.titleTextView
        //villeTextView = binding.villeTextView
        usernameTextView = binding.posterNameTextView
        emailTextView = binding.posterEmailTextView
        descriptionTextView = binding.descriptionTextView
        //locationTextView = binding.locationTextView
        openMapButton = binding.openMapButton
        imageView = binding.imageView
        phoneTextView = binding.posterPhoneTextView

        // Récupération des données passées dans l'intent
        val title = intent.getStringExtra("title") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val latitude = intent.getStringExtra("latitude")
        val longitude = intent.getStringExtra("longitude")
        val imageString = intent.getStringExtra("image")
        val ville = intent.getStringExtra("ville")
        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("email")
        val phone = intent.getStringExtra("phone")



        // Affichage des données
        titleTextView.text = title
        usernameTextView.text = "Posté par : $username"
        emailTextView.text = "Email : $email"
        phoneTextView.text = "Téléphone : $phone"
        descriptionTextView.text = "$description"
        //locationTextView.text = "Position : $latitude, $longitude"
        //villeTextView.text = "Ville : $ville"



        // Affichage de l'image
        // Si l'image est une URL, utilisez Glide pour la charger :
        Glide.with(this)
            .load("https://lbf-ing3-2024.onrender.com"+imageString) // Remplacez "imageUrl" par la propriété réelle de l'URL de l'image
            .centerCrop()  // Optionnel, pour centrer l'image et la couper si elle dépasse.
            .into(imageView)


        // Configurer le clic pour ouvrir Google Maps
        openMapButton.setOnClickListener {
            val gmmIntentUri = Uri.parse("geo:${latitude},${longitude}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps") // Ouvrir avec l'application Google Maps
            startActivity(mapIntent)
        }
    }
}

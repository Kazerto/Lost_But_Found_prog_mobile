package com.example.myapplicationlbf.Response

import com.google.gson.annotations.SerializedName

data class LostItemsResponse(
    val status: String,
    val items: List<LostItem>
)

data class LostItemResponse(
    @SerializedName("_id") val id: String,
    val title: String,
    val description: String,
    val category: String,
    val address: String,
    val contactNumber : String,
    val claimed : Boolean,
    val imageUrl: String? // En base64
)

data class LostItem(
    @SerializedName("_id") val id: String,
    val title: String,
    val description: String,
    val address: String,
    val reportedBy: ReportedBy,  // Créez un objet ReportedBy pour contenir les informations de l'utilisateur
    val imageUrl: String?, // En base64 ou URL
    val  category: String,
    val contactNumber : String,
    val claimed : Boolean,
)

data class ReportedBy(
    val username: String,
    val email: String,
    val phone: String
)


data class User(
    val username: String,
    val email: String,
    val phone: String,
    val city: String
)



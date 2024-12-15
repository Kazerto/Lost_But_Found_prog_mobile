package com.example.myapplicationlbf.Interface

import com.example.myapplicationlbf.Request.ForgotPasswordRequest
import com.example.myapplicationlbf.Request.LoginRequest
import com.example.myapplicationlbf.Request.RegisterRequest
import com.example.myapplicationlbf.Request.ResetPasswordRequest
import com.example.myapplicationlbf.Response.ForgotPasswordResponse
import com.example.myapplicationlbf.Response.LoginResponse
import com.example.myapplicationlbf.Response.LostItemResponse
import com.example.myapplicationlbf.Response.LostItemsResponse
import com.example.myapplicationlbf.Response.ResetPasswordResponse
import com.example.myapplicationlbf.Response.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query


// Définir l'interface pour l'API
interface ApiService {

    // Endpoint pour se connecter, ici on utilise POST et on envoie un objet JSON contenant l'email et le mot de passe
    @POST("login")  // Le chemin de l'API pour la connexion
    fun loginUser(
        @Body credentials: LoginRequest // On passe un objet LoginRequest avec l'email et le mot de passe
    ): Call<LoginResponse>

    @POST("register")
    fun registerUser(
        @Body registerData: RegisterRequest
    ): Call<LoginResponse>  // Reuse LoginResponse for simplicity, or define a separate RegisterResponse class

    // Endpoint pour l'oubli de mot de passe (envoi d'OTP)
    @POST("forgot-password")
    fun forgotPassword(@Body forgotPasswordRequest: ForgotPasswordRequest): Call<ForgotPasswordResponse>

    // Endpoint pour la réinitialisation du mot de passe
    @POST("reset-password")
    fun resetPassword(@Body resetPasswordRequest: ResetPasswordRequest): Call<ResetPasswordResponse>

    @DELETE("lostitemsOwn/{id}")
    fun deleteLostItem(@Header("Authorization") token: String, @Path("id") itemId: String): Call<LostItemsResponse>

    // Méthode pour récupérer les objets perdus avec un terme de recherche
    @GET("lostitems")
    suspend fun getLostItems(
        @Header("Authorization") authToken: String,  // Token d'authentification
        @Query("search") searchQuery: String? = ""  // Paramètre de recherche (optionnel)
    ): Response<LostItemsResponse>

    @GET("lost-items")  // L'URL exacte de votre API pour récupérer les objets perdus
    fun getLostItems(): Call<List<LostItemResponse>>

    @GET("/lost-items/search")
    fun searchLostItems(@Query("title") title: String?,
                        @Query("description") description: String?,
                        @Query("address") address: String?): Call<List<LostItemResponse>>

    // Méthode pour rechercher les objets perdus en fonction du terme de recherche
    @GET("search-lost-items")
    fun searchLostItems(@Query("q") query: String): Call<List<LostItemResponse>>

    @GET("lostitemsOwn")
    suspend fun getLostItemsOwn(
        @Header("Authorization") authToken: String,  // Token d'authentification
        @Query("search") searchQuery: String? = ""  // Paramètre de recherche (optionnel)
    ): Response<LostItemsResponse>


    @GET("user/profile")
    suspend fun getUserProfile(@Header("Authorization") authToken: String): Response<User>// User représente le modèle de données de l'utilisateur


    @GET("verify-token")
    suspend fun verifyTokens(@Header("Authorization") authToken: String): Response<LoginResponse>

    // Définir une méthode pour mettre à jour le profil de l'utilisateur
    @PUT("/user/profile")
    suspend fun updateUserProfile(
        @Header("Authorization") authToken: String,
        @Body user: User
    ): Response<User>

    @Multipart
    @POST("report-lost-item")
    fun reportLostItem(

        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part("address") address: RequestBody,
        @Part("category") categorie: RequestBody,
        @Part("contactNumber") contact: RequestBody,
        @Part image: MultipartBody.Part?,
        @Header("Authorization") authToken: String  // Token d'authentification
    ): Call<LostItemResponse>
}

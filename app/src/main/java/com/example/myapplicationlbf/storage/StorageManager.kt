package com.example.myapplicationlbf.storage

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

object StorageManager {

    private val storage = FirebaseStorage.getInstance()

    /**
     * Lister les fichiers dans un dossier
     */
    fun listFiles(
        folderPath: String,
        onSuccess: (List<StorageReference>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val storageRef = storage.reference.child(folderPath)
        storageRef.listAll()
            .addOnSuccessListener { listResult ->
                onSuccess(listResult.items) // Retourne les fichiers
            }
            .addOnFailureListener { exception ->
                onFailure(exception) // Gérer les erreurs
            }
    }

    /**
     * Télécharger un fichier
     */
    fun downloadFile(
        filePath: String,
        localUri: Uri,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fileRef = storage.reference.child(filePath)
        fileRef.getFile(localUri)
            .addOnSuccessListener {
                onSuccess() // Téléchargement réussi
            }
            .addOnFailureListener { exception ->
                onFailure(exception) // Gérer les erreurs
            }
    }

    /**
     * Supprimer un fichier
     */
    fun deleteFile(
        filePath: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val fileRef = storage.reference.child(filePath)
        fileRef.delete()
            .addOnSuccessListener {
                onSuccess() // Suppression réussie
            }
            .addOnFailureListener { exception ->
                onFailure(exception) // Gérer les erreurs
            }
    }
}

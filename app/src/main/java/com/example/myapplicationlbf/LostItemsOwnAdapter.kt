package com.example.myapplicationlbf

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationlbf.Response.LostItem
import com.bumptech.glide.Glide // Si vous utilisez Glide pour charger l'image.
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener

class LostItemsOwnAdapter(
    private var items: List<LostItem>,
    private val onItemClick: (LostItem) -> Unit,  // pour le clic sur l'élément
    private val onDeleteClick: (LostItem) -> Unit, // pour le clic sur supprimer
    private val statusClickListener: (LostItem, Boolean) -> Unit
) : RecyclerView.Adapter<LostItemsOwnAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Liaison des vues
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val btnStatus: Button = view.findViewById(R.id.btnStatus)
        val tvImage: ImageView = view.findViewById(R.id.imageViewItem)
        val deleteButton: Button = view.findViewById(R.id.deleteButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lostown_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // Affectation des valeurs
        holder.tvTitle.text = item.title
        holder.tvCategory.text = item.category
        holder.btnStatus.text = if (item.claimed) "UNAVAILABLE" else "AVAILABLE" // Changer le texte du bouton en fonction du statut

        val imageUrl = "https://lbf-ing3-2024.onrender.com${item.imageUrl}"
        Log.d("ImageLoading", "chargement de l'image a partir de l'url: $imageUrl")

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("ImageLoading", "Error loading image: ${e?.localizedMessage}")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: com.bumptech.glide.load.DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("ImageLoading", "Image loaded successfully")
                    return false
                }
            })
            .into(holder.tvImage)

        // Définir un écouteur de clic pour chaque élément
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
        // Gérer le clic sur le bouton de suppression
        holder.deleteButton.setOnClickListener {
            onDeleteClick(item)
        }

        holder.btnStatus.setOnClickListener {
            val newStatus = !item.claimed // Inverse la valeur actuelle
            statusClickListener(item, newStatus)
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<LostItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}

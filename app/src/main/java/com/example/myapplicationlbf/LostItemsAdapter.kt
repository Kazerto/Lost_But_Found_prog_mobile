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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.example.myapplicationlbf.Response.LostItem
import javax.sql.DataSource

class LostItemsAdapter(
    private var items: List<LostItem>,
    private val onItemClick: (LostItem) -> Unit // Ajout de l'écouteur de clic
) : RecyclerView.Adapter<LostItemsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Liaison des vues
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvImage: ImageView = view.findViewById(R.id.imageViewItem)
        val tvReportedBy: TextView = view.findViewById(R.id.tvReportedBy)
        val btnStatus: Button = view.findViewById(R.id.btnStatus) // Correct ID
    }

    // Cette méthode est appelée pour créer un nouveau ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_lost_item, parent, false)
        return ViewHolder(view)
    }

    // Cette méthode est appelée pour remplir les données dans chaque item du RecyclerView
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        // Affectation des valeurs
        holder.tvTitle.text = item.title
        holder.tvCategory.text = item.category
        holder.tvReportedBy.text = item.reportedBy.email // Supposons que "reportedBy" est un objet contenant un email
        holder.btnStatus.text = if (item.claimed) "AVAILABLE" else "UNAVAILABLE" // Changer le texte du bouton en fonction du statut

        // Dans onBindViewHolder
        // Dans onBindViewHolder de LostItemsAdapter, avant le chargement Glide
        // Dans onBindViewHolder de LostItemsAdapter
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
    }

    // Cette méthode retourne le nombre d'éléments dans la liste
    override fun getItemCount(): Int = items.size

    // Méthode pour mettre à jour les items dans l'adaptateur
    fun updateItems(newItems: List<LostItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}

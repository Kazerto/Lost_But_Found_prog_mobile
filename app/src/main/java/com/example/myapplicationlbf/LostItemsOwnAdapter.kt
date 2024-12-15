package com.example.myapplicationlbf

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

class LostItemsOwnAdapter(
    private var items: List<LostItem>,
    private val onItemClick: (LostItem) -> Unit,  // pour le clic sur l'élément
    private val onDeleteClick: (LostItem) -> Unit // pour le clic sur supprimer
) : RecyclerView.Adapter<LostItemsOwnAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Liaison des vues
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val btnStatus: Button = view.findViewById(R.id.btnStatus)
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
        holder.btnStatus.text = if (item.claimed) "AVAILABLE" else "UNAVAILABLE" // Changer le texte du bouton en fonction du statut

        // Gérer le clic sur le bouton de suppression
        holder.deleteButton.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<LostItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}

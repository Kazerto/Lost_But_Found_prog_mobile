package com.example.myapplicationlbf

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplicationlbf.Response.LostItem

class LostItemsAdapter(
    private var items: List<LostItem>,
    private val onItemClick: (LostItem) -> Unit // Ajout de l'écouteur de clic
) : RecyclerView.Adapter<LostItemsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Liaison des vues
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
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

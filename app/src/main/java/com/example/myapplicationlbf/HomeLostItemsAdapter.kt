package com.example.myapplicationlbf

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplicationlbf.Response.LostItemResponse
import com.example.myapplicationlbf.databinding.ItemListItemBinding


class HomeLostItemsAdapter(private val lostItems: List<LostItemResponse>) :
    RecyclerView.Adapter<HomeLostItemsAdapter.LostItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LostItemViewHolder {
        val binding = ItemListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LostItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LostItemViewHolder, position: Int) {
        val item = lostItems[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = lostItems.size

    inner class LostItemViewHolder(private val binding: ItemListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LostItemResponse) {
            binding.tvTitle.text = item.title
            binding.tvCategory.text = item.category
            binding.tvReportedBy.text = "Reported by: ${item.contactNumber ?: "N/A"}"
            binding.btnStatus.setText(if (item.claimed) "AVAILABLE" else "UNAVAILABLE");

            if (!item.imageUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(item.imageUrl)
                    .into(binding.imageViewItem)
            } else {
                binding.imageViewItem.setImageResource(R.drawable.logo) // Image par défaut
            }
        }

    }
}



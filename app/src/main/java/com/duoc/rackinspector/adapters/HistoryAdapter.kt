package com.duoc.rackinspector.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.duoc.rackinspector.data.model.RackAssessment
import com.duoc.rackinspector.databinding.ItemHistoryBinding

class HistoryAdapter(
    private val assessments: List<RackAssessment>,
    private val onItemClick: (assessment: RackAssessment) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    inner class HistoryViewHolder(val binding: ItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = assessments[position]
        holder.binding.tvHistoryRackTitle.text = "${item.rackName} - ${item.location}"
        holder.binding.tvHistoryDate.text = item.date
        holder.binding.tvHistoryUser.text = "Usuario: ${item.userEmail}"

        if (item.imageUri.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(Uri.parse(item.imageUri))
                .into(holder.binding.ivHistoryThumbnail)
        }

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount(): Int = assessments.size
}
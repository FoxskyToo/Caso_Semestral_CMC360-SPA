package com.duoc.rackinspector.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.duoc.rackinspector.data.model.ComponentItem
import com.duoc.rackinspector.databinding.ItemComponentBinding

class ComponentAdapter(
    private val items: MutableList<ComponentItem>,
    private val onEditClick: (position: Int, item: ComponentItem) -> Unit,
    private val onDeleteClick: (position: Int) -> Unit
) : RecyclerView.Adapter<ComponentAdapter.ComponentViewHolder>() {

    inner class ComponentViewHolder(val binding: ItemComponentBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComponentViewHolder {
        val binding = ItemComponentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ComponentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ComponentViewHolder, position: Int) {
        val item = items[position]
        holder.binding.tvCategoryName.text = item.category
        holder.binding.tvComponentCount.text = "x${item.count}"

        holder.binding.btnEditComponent.setOnClickListener {
            onEditClick(holder.adapterPosition, item)
        }

        holder.binding.btnDeleteComponent.setOnClickListener {
            onDeleteClick(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int = items.size
}
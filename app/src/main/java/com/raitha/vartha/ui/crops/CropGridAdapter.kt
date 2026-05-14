package com.raitha.vartha.ui.crops

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.raitha.vartha.databinding.ItemCropGridBinding
import com.raitha.vartha.models.Crop

class CropGridAdapter(
    private val onItemClick: (Crop) -> Unit
) : ListAdapter<Crop, CropGridAdapter.ViewHolder>(CropDiffCallback()) {

    class ViewHolder(val binding: ItemCropGridBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCropGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val crop = getItem(position)
        holder.binding.apply {
            tvCropName.text = "${crop.name} (${crop.nameKn})"
            tvCategory.text = crop.categoryKn
            
            // Map local drawable name to resource ID
            val context = ivCrop.context
            val resourceId = context.resources.getIdentifier(crop.imageName, "drawable", context.packageName)
            
            // Efficient image loading
            Glide.with(ivCrop.context)
                .load(if (resourceId != 0) resourceId else crop.imageName) // Fallback if not found
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(ivCrop)

            root.setOnClickListener { onItemClick(crop) }
        }
    }

    class CropDiffCallback : DiffUtil.ItemCallback<Crop>() {
        override fun areItemsTheSame(oldItem: Crop, newItem: Crop): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Crop, newItem: Crop): Boolean = oldItem == newItem
    }
}

package com.raitha.vartha.ui.saved

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raitha.vartha.databinding.ItemSuccessStoryBinding
import com.raitha.vartha.models.SuccessStory

class StoryAdapter(
    private val onStoryClick: (SuccessStory) -> Unit
) : ListAdapter<SuccessStory, StoryAdapter.ViewHolder>(StoryDiffCallback()) {

    class ViewHolder(val binding: ItemSuccessStoryBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSuccessStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val story = getItem(position)
        holder.binding.apply {
            tvFarmerName.text = story.farmerName
            tvDistrict.text = story.district
            tvCropType.text = story.cropType
            tvStory.text = story.story
            tvYield.text = "Result: ${story.yieldIncrease}"

            val context = ivStory.context
            val resourceName = story.imageUrl.lowercase().replace(" ", "")
            val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)

            Glide.with(context)
                .load(if (resourceId != 0) resourceId else story.imageUrl)
                .centerCrop()
                .placeholder(com.google.android.material.R.color.material_dynamic_neutral90)
                .into(ivStory)
                
            root.setOnClickListener { onStoryClick(story) }
        }
    }

    class StoryDiffCallback : DiffUtil.ItemCallback<SuccessStory>() {
        override fun areItemsTheSame(oldItem: SuccessStory, newItem: SuccessStory): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: SuccessStory, newItem: SuccessStory): Boolean = oldItem == newItem
    }
}

package com.raitha.vartha.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.raitha.vartha.databinding.ItemFlashCardBinding
import com.raitha.vartha.models.Tip

class FlashCardAdapter(
    private val onSaveClick: (Tip) -> Unit,
    private val onShareClick: (Tip) -> Unit,
    private val onListenClick: (String) -> Unit
) : ListAdapter<Tip, FlashCardAdapter.ViewHolder>(TipDiffCallback()) {

    class ViewHolder(val binding: ItemFlashCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFlashCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tip = getItem(position)
        holder.binding.apply {
            llContent.alpha = 0f
            llContent.animate().alpha(1f).setDuration(500).start()

            tvCropName.text = "${tip.cropName}\n(${tip.cropNameKn})"
            tvTipTitle.text = tip.tipTitleKn
            tvTipDescription.text = tip.tipDescriptionKn
            tvAiRecommendation.text = tip.aiRecommendationKn ?: "ಮಾಹಿತಿ ಲಭ್ಯವಿಲ್ಲ..."
            tvWeatherWarning.text = tip.weatherAlertKn ?: "ಸಾಮಾನ್ಯ ಹವಾಮಾನ"
            
            chipCategory.text = "CROP TIP" // Could be dynamic if Tip model had category

            // Map local drawable name
            val context = ivCrop.context
            val resourceId = context.resources.getIdentifier(tip.imageUrl, "drawable", context.packageName)

            Glide.with(context)
                .load(if (resourceId != 0) resourceId else tip.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(ivCrop)

            btnSave.setOnClickListener { onSaveClick(tip) }
            btnShare.setOnClickListener { onShareClick(tip) }
            btnListen.setOnClickListener { 
                val textToRead = "${tip.cropNameKn}. ${tip.tipTitleKn}. ${tip.tipDescriptionKn}"
                onListenClick(textToRead)
            }
        }
    }

    class TipDiffCallback : DiffUtil.ItemCallback<Tip>() {
        override fun areItemsTheSame(oldItem: Tip, newItem: Tip): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Tip, newItem: Tip): Boolean = oldItem == newItem
    }
}

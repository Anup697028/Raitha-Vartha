package com.raitha.vartha.ui.expert

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.raitha.vartha.databinding.ItemChatMessageBinding
import com.raitha.vartha.models.ChatMessage

class ChatAdapter : ListAdapter<ChatMessage, ChatAdapter.ViewHolder>(ChatDiffCallback()) {

    class ViewHolder(val binding: ItemChatMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = getItem(position)
        holder.binding.apply {
            tvMessage.text = message.text
            
            val params = cvMessage.layoutParams as ConstraintLayout.LayoutParams
            if (message.isUser) {
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                params.startToStart = ConstraintLayout.LayoutParams.UNSET
                params.startToEnd = ConstraintLayout.LayoutParams.UNSET
                
                ivAvatar.visibility = View.GONE
                cvMessage.setCardBackgroundColor(0xFFE8F5E9.toInt())
                tvMessage.setTextColor(0xFF2E7D32.toInt())
            } else {
                params.startToEnd = ivAvatar.id
                params.startToStart = ConstraintLayout.LayoutParams.UNSET
                params.endToEnd = ConstraintLayout.LayoutParams.UNSET
                
                ivAvatar.visibility = View.VISIBLE
                cvMessage.setCardBackgroundColor(0xFFF5F5F5.toInt())
                tvMessage.setTextColor(0xFF212121.toInt())
            }
            cvMessage.layoutParams = params
        }
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean = 
            oldItem.timestamp == newItem.timestamp
        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean = 
            oldItem == newItem
    }
}

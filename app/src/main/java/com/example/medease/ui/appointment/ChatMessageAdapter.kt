package com.example.medease.ui.appointment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R

class ChatMessageAdapter(
    private var messages: List<ChatMessage>
) : RecyclerView.Adapter<ChatMessageAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvMessage: TextView = view.findViewById(R.id.tv_message)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].isSender) 1 else 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val layout = if (viewType == 1)
            R.layout.item_chat_right
        else
            R.layout.item_chat_left

        val view = LayoutInflater.from(parent.context).inflate(layout, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.tvMessage.text = messages[position].message
    }

    fun updateData(newList: List<ChatMessage>) {
        messages = newList
        notifyDataSetChanged()
    }
}

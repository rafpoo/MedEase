package com.example.medease.ui.appointment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R

class ChatListAdapter(
    private var list: List<ChatItem>,
    private val onClick: (ChatItem) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    class ChatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tv_patient_name)
        val lastMessage: TextView = view.findViewById(R.id.tv_last_message)
        val time: TextView = view.findViewById(R.id.tv_last_time)
        val photo: ImageView = view.findViewById(R.id.img_patient_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_list, parent, false)
        return ChatViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = list[position]
        holder.name.text = item.name
        holder.lastMessage.text = item.lastMessage
        holder.time.text = item.lastTime

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    fun update(newList: List<ChatItem>) {
        list = newList
        notifyDataSetChanged()
    }
}

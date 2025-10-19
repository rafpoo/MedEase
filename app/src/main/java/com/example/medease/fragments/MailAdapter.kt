package com.example.medease.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medease.R

class MailAdapter(
    private val mailList: List<Mail>,
    private val onClick: (Mail) -> Unit
) : RecyclerView.Adapter<MailAdapter.MailViewHolder>() {

    inner class MailViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_mail_title)
        val snippet: TextView = view.findViewById(R.id.tv_mail_snippet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mail, parent, false)
        return MailViewHolder(view)
    }

    override fun onBindViewHolder(holder: MailViewHolder, position: Int) {
        val mail = mailList[position]
        holder.title.text = mail.title
        holder.snippet.text = mail.content
        holder.itemView.setOnClickListener {
            onClick(mail)
        }
    }

    override fun getItemCount() = mailList.size
}

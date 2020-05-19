package com.homindolentrahar.mlkitseries.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.homindolentrahar.mlkitseries.R
import com.homindolentrahar.mlkitseries.model.SmartReplyModel
import kotlinx.android.synthetic.main.list_item_chat.view.*

class SmartReplyAdapter(
    private val context: Context,
    private val conversations: List<SmartReplyModel>
) : RecyclerView.Adapter<SmartReplyAdapter.SmartReplyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SmartReplyHolder {
        return SmartReplyHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item_chat, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    override fun onBindViewHolder(holder: SmartReplyHolder, position: Int) {
        val item = conversations[position]
        holder.bind(item)
    }

    inner class SmartReplyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: SmartReplyModel) {
            if (item.isLocalUser) {
                itemView.img_avatar.setBackgroundColor(context.getColor(R.color.colorPrimary))
            } else {
                itemView.img_avatar.setBackgroundColor(context.getColor(R.color.colorAccent))
            }
            itemView.tv_sender.text = if (item.isLocalUser) "You" else "Google Asst"
            itemView.tv_message.text = item.message
        }
    }
}
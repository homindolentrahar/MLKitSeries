package com.homindolentrahar.mlkitseries.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.homindolentrahar.mlkitseries.R
import com.homindolentrahar.mlkitseries.model.ImageLabelingModel
import kotlinx.android.synthetic.main.list_item_label.view.*

class ImageLabelAdapter :
    ListAdapter<ImageLabelingModel, ImageLabelAdapter.ImageLabelHolder>(ImageLabelComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageLabelHolder {
        return ImageLabelHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_label, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ImageLabelHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ImageLabelHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(item: ImageLabelingModel) {
            itemView.tv_object.text = item.text
            itemView.tv_confidence.text = item.confidence.toString()
        }
    }

    companion object ImageLabelComparator : DiffUtil.ItemCallback<ImageLabelingModel>() {
        override fun areItemsTheSame(
            oldItem: ImageLabelingModel,
            newItem: ImageLabelingModel
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: ImageLabelingModel,
            newItem: ImageLabelingModel
        ): Boolean = oldItem == newItem
    }
}
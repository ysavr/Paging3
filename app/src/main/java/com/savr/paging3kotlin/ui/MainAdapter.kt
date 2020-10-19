package com.savr.paging3kotlin.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.savr.paging3kotlin.R
import com.savr.paging3kotlin.data.response.Data
import kotlinx.android.synthetic.main.item_list.view.*

class MainAdapter : PagingDataAdapter<Data, MainAdapter.ViewHolder>(DataDifferentiator){

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val text = "${getItem(position)?.firstName} ${getItem(position)?.lastName}"
        holder.itemView.textViewName.text = text
        holder.itemView.textViewEmail.text = getItem(position)?.email
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_list, parent, false)
        )
    }

    object DataDifferentiator : DiffUtil.ItemCallback<Data>() {

        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }
    }
}
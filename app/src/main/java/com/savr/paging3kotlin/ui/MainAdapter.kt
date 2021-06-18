package com.savr.paging3kotlin.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.savr.paging3kotlin.R
import com.savr.paging3kotlin.model.MovieData
import kotlinx.android.synthetic.main.item_movie.view.*

class MainAdapter : PagingDataAdapter<MovieData, MainAdapter.ViewHolder>(DataDifferentiator) {

    companion object {
        private const val IMAGE_URL = "https://image.tmdb.org/t/p/w92/"
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val image: ImageView = view.iv_thumbnail
        private val title: TextView = view.tv_title
        private val date: TextView = view.tv_date
        private var movie: MovieData? = null

        fun bind(it: MovieData) {
            Glide.with(itemView.context)
                .load(IMAGE_URL + it.posterPath)
                .into(image)
            title.text = it.title
            date.text = it.releaseDate
            this.movie = it
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_movie, parent, false)
        )
    }

    object DataDifferentiator : DiffUtil.ItemCallback<MovieData>() {

        override fun areItemsTheSame(oldItem: MovieData, newItem: MovieData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieData, newItem: MovieData): Boolean {
            return oldItem == newItem
        }
    }
}
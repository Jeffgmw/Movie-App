package com.example.starstream.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.starstream.databinding.ItemMovieBinding
import com.example.starstream.databinding.ItemTrendingMovieBinding
import com.example.starstream.domain.model.Movie

class MovieAdapter(
    private val isGrid: Boolean = false,
    private val isCredits: Boolean = false,
    private val isTrending: Boolean = false,
    private val onTrendingFabClick: ((Int) -> Unit)? = null,
    private val onLoadMore: (() -> Unit)? = null,
    private val onMovieClick: ((Movie) -> Unit)? = null
) : ListAdapter<Movie, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    inner class HorizontalViewHolder private constructor(val view: ItemMovieBinding) : RecyclerView.ViewHolder(view.root) {
        constructor(parent: ViewGroup) : this(ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)) {
            view.root.setOnClickListener {
                onMovieClick?.invoke(getItem(adapterPosition))
            }
        }
    }

    inner class TrendingViewHolder private constructor(val view: ItemTrendingMovieBinding) : RecyclerView.ViewHolder(view.root) {
        constructor(parent: ViewGroup) : this(ItemTrendingMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)) {
            view.fabTrailer.setOnClickListener {
                onTrendingFabClick?.invoke(getItem(adapterPosition).id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (isTrending) TrendingViewHolder(parent) else HorizontalViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is HorizontalViewHolder -> {
                holder.view.apply {
                    isGrid = this@MovieAdapter.isGrid
                    isCredits = this@MovieAdapter.isCredits
                    movie = getItem(position)
                }
            }
            is TrendingViewHolder -> holder.view.movie = getItem(position)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }
}
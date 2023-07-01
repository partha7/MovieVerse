package com.example.movieverse.ui.list

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieverse.R
import com.example.movieverse.data.models.Movie
import com.example.movieverse.databinding.MovieItemLayoutBinding

/**
 * Adapter class for the movie list RecyclerView.
 */
class MovieListAdapter(val context: Context) :
    PagingDataAdapter<Movie, MovieListAdapter.MovieViewHolder>(diffUtil) {

    /**
     * Creates a new ViewHolder instance for the movie list item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MovieItemLayoutBinding.inflate(inflater, parent, false)
        return MovieViewHolder(binding)
    }

    /**
     * Binds the movie data to the ViewHolder.
     */
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        movie?.let { holder.bind(it) }
    }

    /**
     * ViewHolder class representing a movie list item.
     */
    inner class MovieViewHolder(val binding: MovieItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("DiscouragedApi")
        fun bind(movie: Movie) {
            //Get the resource name from the poster image field
            val resourceName = movie.posterImage.split(".")[0]

            // Get the resource ID using the resource name
            var resourceId: Int =
                context.resources.getIdentifier(resourceName, "drawable", context.packageName)

            // Use a placeholder drawable if the resource ID is not found
            if (resourceId == 0) resourceId = R.drawable.placeholder_for_missing_posters

            binding.movieTitle.text = movie.name
            Glide.with(binding.root)
                .load(AppCompatResources.getDrawable(context, resourceId))
                .into(binding.moviePoster)
        }
    }

    companion object {
        /**
         * DiffUtil instance for calculating the difference between old and new movie items.
         */
        val diffUtil = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }


}

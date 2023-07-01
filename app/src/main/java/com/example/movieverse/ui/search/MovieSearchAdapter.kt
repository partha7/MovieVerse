package com.example.movieverse.ui.search

import android.annotation.SuppressLint
import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieverse.R
import com.example.movieverse.data.viewmodels.SearchResultMovie
import com.example.movieverse.databinding.MovieItemLayoutBinding

/**
 * Adapter for displaying movies in search results.
 *
 * @param context The context of the adapter.
 */
class MovieSearchAdapter(val context: Context) :
    RecyclerView.Adapter<MovieSearchAdapter.MovieViewHolder>() {

    /**
     * DiffUtil instance for calculating the difference between old and new search results.
     */
    private val diffUtil = object : DiffUtil.ItemCallback<SearchResultMovie>() {
        override fun areItemsTheSame(
            oldItem: SearchResultMovie,
            newItem: SearchResultMovie
        ): Boolean {
            return oldItem.name == newItem.name && oldItem.searchTerm == newItem.searchTerm
        }

        override fun areContentsTheSame(
            oldItem: SearchResultMovie,
            newItem: SearchResultMovie
        ): Boolean {
            return oldItem == newItem
        }
    }

    //List storing the search results
    private val searchResults: AsyncListDiffer<SearchResultMovie> = AsyncListDiffer(this, diffUtil)

    /**
     * Creates a new ViewHolder instance for the movie list item.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MovieItemLayoutBinding.inflate(inflater, parent, false)
        return MovieViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchResults.currentList.size
    }

    /**
     * Binds the movie data to the ViewHolder.
     */
    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = searchResults.currentList[position]
        movie?.let { holder.bind(it) }
    }


    /**
     * Setting new search data.
     */
    fun setData(searchResultList: List<SearchResultMovie>?) {
        if (searchResultList != null) {
            searchResults.submitList(searchResultList)
        }
    }


    /**
     * ViewHolder class representing a movie list item.
     */
    inner class MovieViewHolder(private val binding: MovieItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("DiscouragedApi")
        fun bind(movie: SearchResultMovie) {
            val resourceName = movie.posterImage.split(".")[0]
            var resourceId: Int =
                context.resources.getIdentifier(resourceName, "drawable", context.packageName)
            if (resourceId == 0) resourceId = R.drawable.placeholder_for_missing_posters

            val spannableString = getSpannableStringForTitle(movie)

            binding.movieTitle.text = spannableString
            Glide.with(binding.root)
                .load(AppCompatResources.getDrawable(context, resourceId))
                .into(binding.moviePoster)
        }

        /**
         * Creates and returns a spannable string for highlighting the search text in the movie title
         * */
        private fun getSpannableStringForTitle(movie: SearchResultMovie): SpannableString {
            val spannableString = SpannableString(movie.name)

            // Specify the color for the search portion of the text
            val textColor = ContextCompat.getColor(context, R.color.search_text_colour)

            // Specify the start and end indices of the portion to color
            val startIndex = movie.name.indexOf(movie.searchTerm, ignoreCase = true)
            val endIndex = startIndex + movie.searchTerm.length

            if (startIndex >= 0 && endIndex <= movie.name.length)
            // Apply the color to that portion of the text
                spannableString.setSpan(
                    ForegroundColorSpan(textColor),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

            return spannableString
        }
    }
}

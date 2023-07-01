package com.example.movieverse.ui.list

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieverse.R
import com.example.movieverse.data.viewmodels.MovieViewModel
import com.example.movieverse.databinding.FragmentMovieListBinding
import com.example.movieverse.ui.search.MovieSearchFragment
import com.example.movieverse.utils.Logger
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieListFragment : Fragment() {

    private lateinit var binding: FragmentMovieListBinding

    private val viewModel by viewModels<MovieViewModel>()
    private lateinit var movieListAdapter: MovieListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieListBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()

        //fetch movie list
        viewModel.fetchMovies()
    }

    /**
     * Sets up the views and click listeners.
     */
    private fun setupViews() {

        //setup recycler view
        initRecyclerView()

        setupObservers()

        binding.toolbar.search.setOnClickListener {
            // Open movie search fragment when the search icon is clicked
            val fragmentManager = activity?.supportFragmentManager?.beginTransaction()
            val movieSearchFragment = MovieSearchFragment()
            fragmentManager?.add(R.id.container, movieSearchFragment)
            fragmentManager?.addToBackStack("MovieListFragment")
            fragmentManager?.commit()
        }

        binding.toolbar.back.setOnClickListener {
            requireActivity().finish()
        }
    }

    /**
     * Initializes the RecyclerView for displaying movies.
     */
    private fun initRecyclerView() {
        binding.apply {
            // Get the current orientation
            val orientation = resources.configuration.orientation

            // Set the appropriate span count based on the orientation
            val spanCount = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                3 // 3 columns for portrait
            } else {
                5 // 5 columns for landscape
            }
            movieListAdapter = MovieListAdapter(requireContext())
            recyclerview.layoutManager = GridLayoutManager(requireContext(), spanCount)
            recyclerview.adapter = movieListAdapter
        }
    }

    /**
     * Sets up observers for movie list data and page title.
     */
    private fun setupObservers() {
        viewModel.moviesList.observe(viewLifecycleOwner) {
            movieListAdapter.submitData(lifecycle, it)
        }

        viewModel.pageTitle.observe(viewLifecycleOwner) {
            binding.toolbar.pageTitle.text = it
        }
    }
}
package com.example.movieverse.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.movieverse.R
import com.example.movieverse.databinding.ActivityMainBinding
import com.example.movieverse.ui.list.MovieListFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openMovieListFragment()
    }

    /**
     * Opens fragment to show movie list.
     * */
    private fun openMovieListFragment() {
        val fragmentManager = supportFragmentManager.beginTransaction()
        val movieListFragment = MovieListFragment()
        fragmentManager.add(R.id.container, movieListFragment)
        fragmentManager.commit()
    }
}

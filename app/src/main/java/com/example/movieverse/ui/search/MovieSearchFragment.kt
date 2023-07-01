package com.example.movieverse.ui.search

import android.content.Context
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieverse.R
import com.example.movieverse.data.viewmodels.MovieSearchViewModel
import com.example.movieverse.data.viewmodels.SearchResult
import com.example.movieverse.databinding.FragmentMovieSearchBinding
import com.example.movieverse.utils.Logger
import com.example.movieverse.utils.hide
import com.example.movieverse.utils.show
import com.example.movieverse.utils.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class MovieSearchFragment : Fragment() {

    /**
     * Instance of the [Logger] class used for logging data.
     * */
    private val logger = Logger(javaClass.simpleName)

    private lateinit var binding: FragmentMovieSearchBinding

    lateinit var movieSearchAdapter: MovieSearchAdapter

    private val movieSearchViewModel: MovieSearchViewModel by viewModels()

    @Inject
    lateinit var compositeDisposable: CompositeDisposable

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 300L
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
    }


    /**
     * Sets up the views and click listeners.
     */
    private fun setupViews() {
        binding.apply {

            //setup recycler view
            initRecyclerView()

            //Observing search results
            setupSearchResultObserver()

            toolbar.clear.setOnClickListener {
                toolbar.search.text = null
                movieSearchViewModel.moviesSearchList.value = SearchResult(emptyList(), "")
                showKeyBoard(toolbar.search)
            }

            toolbar.search.apply {
                //whenever this screen opens, the search bar is active with open keyboard
                requestFocus()
                showKeyBoard(this)

                //subscribing to search bar text changes
                compositeDisposable.add(
                    textChanges()
                        .debounce(SEARCH_DEBOUNCE_DELAY, TimeUnit.MILLISECONDS)
                        .distinctUntilChanged()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ searchText ->
                            //make a search only when the search term char length is more than 3
                            //otherwise return an empty list
                            if (searchText.length >= 3) movieSearchViewModel.searchMovies(searchText)
                            else movieSearchViewModel.moviesSearchList.value =
                                SearchResult(emptyList(), searchText)
                        }, {
                            logger.error("Error getting search bar text changes", it)
                        })
                )
            }

            //checking for soft keyboard visibility to keep search bar cursor visible only when keyboard is open
            containerSearchFragment.apply {
                viewTreeObserver.addOnGlobalLayoutListener {
                    val rec = Rect()
                    getWindowVisibleDisplayFrame(rec)

                    //finding screen height
                    val screenHeight = rootView.height

                    //finding keyboard height
                    val keypadHeight = screenHeight - rec.bottom

                    //cursor should be visible only when keyboard is visible
                    toolbar.search.isCursorVisible = keypadHeight > screenHeight * 0.15
                }
            }
        }
    }

    /**
     * Setup observer for search results
     * */
    private fun setupSearchResultObserver() {
        binding.apply {
            movieSearchViewModel.moviesSearchList.observe(viewLifecycleOwner) {
                movieSearchAdapter.setData(it)
                //showing min character message when search term is small than 3 characters
                if (it.searchTerm.length < 3) {
                    recyclerview.hide()
                    searchInfoTv.show()
                    searchInfoTv.text = getString(R.string.search_string_length_warning)
                }
                //showing no results message when search results is empty
                else if (it.searchResultList.isEmpty()) {
                    recyclerview.hide()
                    searchInfoTv.show()
                    searchInfoTv.text = getString(R.string.oops_no_search_results_found)
                }
                //else showing rv with search results
                else {
                    searchInfoTv.hide()
                    recyclerview.show()
                }
            }
        }
    }

    /**
     * Setup recycler view for search results
     * */
    private fun initRecyclerView() {
        binding.apply {
            movieSearchAdapter = MovieSearchAdapter(requireContext())

            //Get the current orientation
            val orientation = resources.configuration.orientation
            //Set the appropriate span count based on the orientation
            val spanCount = if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                3 // 3 columns for portrait
            } else {
                5 // 5 columns for landscape
            }
            recyclerview.layoutManager = GridLayoutManager(requireContext(), spanCount)
            recyclerview.adapter = movieSearchAdapter
        }
    }

    /**
     * Shows keyboard for the passed view
     * */
    private fun showKeyBoard(view: View) {
        view.let {
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    /**
     * Hides keyboard for the passed view
     * */
    private fun hideKeyBoard(view: View) {
        view.let {
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onPause() {
        super.onPause()
        hideKeyBoard(binding.toolbar.search)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

}
package com.example.movieverse.data.repositories

import android.content.Context
import com.example.movieverse.data.models.Data
import com.example.movieverse.data.models.Movie
import com.example.movieverse.utils.Constants
import com.example.movieverse.utils.Logger
import com.google.gson.Gson
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

/**
 * Repository class for handling movie data retrieval.
 */
class MovieRepository @Inject constructor(val context: Context, private val gson: Gson) {

    /**
     * Instance of the [Logger] class used for logging data.
     * */
    private val logger = Logger(javaClass.simpleName)

    /**
     * Retrieves all the movies by reading JSON files.
     * @return Observable that emits a list of movies.
     */
    fun getAllMovies(): Observable<List<Movie>> {
        val fileNumbers: List<Int> = (1..Constants.TOTAL_PAGES).toList()
        return Observable.fromIterable(fileNumbers)
            .flatMap {
                val data = readJsonFile("${Constants.FILENAME_PREFIX}${it}.json")
                Observable.just(data?.page?.movies?.content ?: listOf())
            }
            .reduce { accumulator, list ->
                accumulator + list
            }
            .toObservable()
    }

    /**
     * Reads a JSON file and converts it into a [Data] object.
     *
     * @param fileName The name of the JSON file to read.
     * @return The [Data] object representing the JSON file content.
     */
    fun readJsonFile(fileName: String): Data? {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use {
                it.readText()
            }
            gson.fromJson(jsonString, Data::class.java)
        } catch (e: Exception) {
            logger.error("Error while retrieving file $fileName", e)
            null
        }
    }


}
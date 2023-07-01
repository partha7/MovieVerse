package com.example.movieverse

import android.app.Application
import com.example.movieverse.utils.Logger
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MovieApplication : Application() {

    /**
     * Instance of the [Logger] class used for logging data.
     * */
    private val logger = Logger(javaClass.simpleName)

    override fun onCreate() {
        super.onCreate()
        logger.info("Welcome to MovieVerse")
    }
}
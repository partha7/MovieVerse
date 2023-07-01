package com.example.movieverse.utils

import android.util.Log
import com.example.movieverse.R

/**
 * Logger class for logging messages and errors.
 *
 * @param tag The tag to be used for logging.
 */
class Logger(private val tag: String) {

    companion object {
        private const val TAG_PREFIX = "MovieVerse"
    }

    /**
     * Log a debug message.
     *
     * @param message The message to be logged.
     */
    fun debug(message: String) {
        Log.d("$TAG_PREFIX: $tag", message)
    }

    /**
     * Log an information message.
     *
     * @param message The message to be logged.
     */
    fun info(message: String) {
        Log.i("$TAG_PREFIX: $tag", message)
    }

    /**
     * Log a warning message.
     *
     * @param message The message to be logged.
     */
    fun warn(message: String) {
        Log.w("$TAG_PREFIX: $tag", message)
    }

    /**
     * Log an error message with an associated Throwable.
     *
     * @param message The message to be logged.
     * @param error The Throwable associated with the error.
     */
    fun error(message: String, error: Throwable) {
        Log.e("$TAG_PREFIX: $tag", message, error)
    }
}
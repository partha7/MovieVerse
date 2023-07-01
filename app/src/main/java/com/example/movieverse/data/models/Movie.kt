package com.example.movieverse.data.models

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("name")
    val name: String,
    @SerializedName("poster-image")
    val posterImage: String
    )

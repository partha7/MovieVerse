package com.example.movieverse.data.models

import com.google.gson.annotations.SerializedName

data class Data(
    val page: Page
)

data class Page(
    @SerializedName("title")
    val title: String,
    @SerializedName("total-content-items")
    val totalItems: String,
    @SerializedName("page-num")
    val pageNum: String,
    @SerializedName("page-size")
    val pageSize: String,
    @SerializedName("content-items")
    val movies: Movies
)

data class Movies(val content: List<Movie>)
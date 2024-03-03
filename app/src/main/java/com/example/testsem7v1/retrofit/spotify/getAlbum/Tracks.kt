package com.example.testsem7v1.retrofit.spotify.getAlbum

data class Tracks(
    val href: String,
    val items: List<Item>,
    val limit: Int,
    val next: Any,
    val offset: Int,
    val previous: Any,
    val total: Int
)
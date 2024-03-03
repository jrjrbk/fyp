package com.example.testsem7v1.retrofit.spotify.getArtistAlbum

data class getArtistAlbumResponse(
    val href: String,
    val items: List<Item>,
    val limit: Int,
    val next: String,
    val offset: Int,
    val previous: Any,
    val total: Int
)
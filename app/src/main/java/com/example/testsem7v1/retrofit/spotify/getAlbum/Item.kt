package com.example.testsem7v1.retrofit.spotify.getAlbum

data class Item(
    val artists: List<Artist>,
    val available_markets: List<Any>,
    val disc_number: Int,
    val duration_ms: Int,
    val explicit: Boolean,
    val external_urls: ExternalUrlsX,
    val href: String,
    val id: String,
    val is_local: Boolean,
    val name: String,
    val preview_url: Any,
    val track_number: Int,
    val type: String,
    val uri: String
)
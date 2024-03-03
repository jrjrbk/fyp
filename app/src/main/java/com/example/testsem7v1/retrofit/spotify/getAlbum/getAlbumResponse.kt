package com.example.testsem7v1.retrofit.spotify.getAlbum

data class getAlbumResponse(
    val album_type: String,
    val artists: List<Artist>,
    val available_markets: List<Any>,
    val copyrights: List<Copyright>,
    val external_ids: ExternalIds,
    val external_urls: ExternalUrlsX,
    val genres: List<Any>,
    val href: String,
    val id: String,
    val images: List<Image>,
    val label: String,
    val name: String,
    val popularity: Int,
    val release_date: String,
    val release_date_precision: String,
    val total_tracks: Int,
    val tracks: Tracks,
    val type: String,
    val uri: String
)
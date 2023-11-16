package com.example.testsem7v1.retrofit.spotify.recommendation

data class recommendationResponse(
    val seeds: List<Seed>,
    val tracks: List<Track>
)
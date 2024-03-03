package com.example.testsem7v1.ACRCloud.humming

data class Humming(
    val acrid: String,
    val album: Album,
    val artists: List<Artist>,
    val duration_ms: Int,
    val external_ids: ExternalIds,
    val external_metadata: ExternalMetadata,
    val label: String,
    val play_offset_ms: Int,
    val release_date: String,
    val result_from: Int,
    val score: Double,
    val title: String
)
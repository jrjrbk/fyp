package com.example.testsem7v1.ACRCloud

data class Music(
    val acrid: String,
    val album: Album,
    val artists: List<Artist>,
    val db_begin_time_offset_ms: Int,
    val db_end_time_offset_ms: Int,
    val duration_ms: Int,
    val external_ids: ExternalIds,
    val external_metadata: ExternalMetadata,
    val genres: List<Genre>,
    val label: String,
    val play_offset_ms: Int,
    val release_date: String,
    val result_from: Int,
    val sample_begin_time_offset_ms: Int,
    val sample_end_time_offset_ms: Int,
    val score: Int,
    val title: String
)
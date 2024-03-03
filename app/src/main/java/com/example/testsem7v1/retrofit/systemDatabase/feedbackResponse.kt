package com.example.testsem7v1.retrofit.systemDatabase

data class feedbackResponse(
    val feedbackID: Int,
    val AccountID: Int,
    val feedbackType: String,
    val rating: Float,
    val comments: String
)

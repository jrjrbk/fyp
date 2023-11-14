package com.example.testsem7v1.retrofit

import com.google.gson.annotations.SerializedName

data class Users(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("username") val username: String,
    @SerializedName("dateCreated") val dateCreated: String
    )
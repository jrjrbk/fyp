package com.example.testsem7v1.retrofit

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object retrofitInstance {

//    LOGGER
    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    private val client = OkHttpClient.Builder().addInterceptor(retrofitInstance.logging).build()

    val api: retrofitInterface by lazy{

//        val gson =  GsonBuilder().setLenient().create()
        Retrofit.Builder()
            .baseUrl("http://192.168.1.104:3000/")
//            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(retrofitInterface::class.java)
    }

    val spotifyapi: retrofitInterface by lazy{
        Retrofit.Builder()
            .baseUrl("https://api.spotify.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(retrofitInterface::class.java)
    }

}
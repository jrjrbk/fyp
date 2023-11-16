package com.example.testsem7v1.retrofit

import com.example.testsem7v1.retrofit.spotify.album.albumResponse
import com.example.testsem7v1.retrofit.spotify.recommendation.recommendationResponse
import com.example.testsem7v1.retrofit.spotify.sAccessToken
import com.example.testsem7v1.retrofit.spotify.test
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface retrofitInterface {


    // Own RESTAPI
    @Headers("Accept: application/json")
    @POST("/api/v1/users")
     fun createUser(@Body userData: Users): Call<Users>
     @GET("/api/v1/users/{email}")
     suspend fun getUserByEmail(@Path("email") email: String): Response<Users>

     @POST("/api/v1/users/login")
     suspend fun loginUser(@Body loginRequest: LoginRequest): Response<userID>

     @POST("api/v1/users/rate")
     suspend fun userRate(@Body feedbackData: feedbackData): Response<ResponseBody>

     //Musicbrainz API

    @GET("{entity}/")
    suspend fun searchEntity(@Path("entity") entity: String?,
                             @Query("query") searchTerm: String?,
                             @Query("limit") limit: Int,
                             @Query("offset") offset: Int): ResponseBody?


    // Spotify API
    @FormUrlEncoded
    @POST("https://accounts.spotify.com/api/token")
    suspend fun getToken(@Field("grant_type") grant_type:String,
                 @Field("client_id") client_id:String,
                 @Field("client_secret") client_secret:String):Response<sAccessToken>

    @GET("https://api.spotify.com/v1/artists/4Z8W4fKeB5YxbusRsdQVPb")
    suspend fun getArtist(@Header("Authorization") authHeader:String): Response <test>

    //Search
//    @GET("/v1/search/")
//    suspend fun searchSpotify(@Query("q") q: String,
//                              @Query("type") type: String,
//                              @Query("limit") limit: Int,
//                              @Query("offset") offset: Int): Response<ResponseBody>

    @GET("https://api.spotify.com/v1/search")
    suspend fun searchAlbumSpotify(@Header("Authorization") authHeader:String,
                                   @Query("q") q: String,
                                   @Query("type") type: String,
                                   @Query("limit") limit: Int,
                                   @Query("offset") offset: Int): Response<albumResponse>

    @GET("https://api.spotify.com/v1/search")
    suspend fun searchArtistSpotify(@Header("Authorization") authHeader:String,
                                   @Query("q") q: String,
                                   @Query("type") type: String,
                                   @Query("limit") limit: Int,
                                   @Query("offset") offset: Int): Response<albumResponse>

    @GET("https://api.spotify.com/v1/search")
    suspend fun searchTrackSpotify(@Header("Authorization") authHeader:String,
                                    @Query("q") q: String,
                                    @Query("type") type: String,
                                    @Query("limit") limit: Int,
                                    @Query("offset") offset: Int): Response<albumResponse>

    @GET("https://api.spotify.com/v1/recommendations")
    suspend fun getRecommendation(@Header("Authorization") authHeader: String,
                                  @Query("limit") limit: Int = 10,
                                  @Query("seed_artists") seed_artists: String,
                                  @Query("seed_tracks") seed_tracks: String): Response<recommendationResponse>
}
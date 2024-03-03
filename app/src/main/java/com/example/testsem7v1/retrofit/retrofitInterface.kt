package com.example.testsem7v1.retrofit

import com.example.testsem7v1.retrofit.spotify.album.albumResponse
import com.example.testsem7v1.retrofit.spotify.albumTrack.getAlbumTrackResponse
import com.example.testsem7v1.retrofit.spotify.artist.artistResponse
import com.example.testsem7v1.retrofit.spotify.getAlbum.getAlbumResponse
import com.example.testsem7v1.retrofit.spotify.getArtist.getArtistResponse
import com.example.testsem7v1.retrofit.spotify.getArtistAlbum.getArtistAlbumResponse
import com.example.testsem7v1.retrofit.spotify.getSeveralTrack.getSeveralTrackResponse
import com.example.testsem7v1.retrofit.spotify.getTopTrack.getTopTrackResponse
import com.example.testsem7v1.retrofit.spotify.getTrack.getTrackResponse
import com.example.testsem7v1.retrofit.spotify.recommendation.recommendationResponse
import com.example.testsem7v1.retrofit.spotify.relatedArtist.relatedArtistResponse
import com.example.testsem7v1.retrofit.spotify.sAccessToken
import com.example.testsem7v1.retrofit.spotify.test
import com.example.testsem7v1.retrofit.spotify.track.trackResponse
import com.example.testsem7v1.retrofit.systemDatabase.LoginRequest
import com.example.testsem7v1.retrofit.systemDatabase.QueryResponse
import com.example.testsem7v1.retrofit.systemDatabase.Username
import com.example.testsem7v1.retrofit.systemDatabase.Users
import com.example.testsem7v1.retrofit.systemDatabase.aboutResponse
import com.example.testsem7v1.retrofit.systemDatabase.feedbackData
import com.example.testsem7v1.retrofit.systemDatabase.gameCreate
import com.example.testsem7v1.retrofit.systemDatabase.gameData
import com.example.testsem7v1.retrofit.systemDatabase.gameUpdate
import com.example.testsem7v1.retrofit.systemDatabase.addPlaylistData
import com.example.testsem7v1.retrofit.systemDatabase.addSongData
import com.example.testsem7v1.retrofit.systemDatabase.feedbackResponse
import com.example.testsem7v1.retrofit.systemDatabase.imagePath
import com.example.testsem7v1.retrofit.systemDatabase.imagePathResponse
import com.example.testsem7v1.retrofit.systemDatabase.playlistData
import com.example.testsem7v1.retrofit.systemDatabase.playlistDataItem
import com.example.testsem7v1.retrofit.systemDatabase.playlistMetadata
import com.example.testsem7v1.retrofit.systemDatabase.queryData
import com.example.testsem7v1.retrofit.systemDatabase.updatePlaylistData
import com.example.testsem7v1.retrofit.systemDatabase.userID
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Objects

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

    @GET("/api/v1/users/id/{id}")
    suspend fun getUsernameByID(@Path("id") userID: Int): Response<Username>

    @GET("/api/v1/users/game/{id}")
    suspend fun getGame(@Path("id") userID: Int): Response<gameData>

//    @POST("api/v1/users/rate")
//    suspend fun userRate(@Body feedbackData: feedbackData): Response<ResponseBody>
    @POST("/api/v1/users/game")
    suspend fun addGame(@Body gameCreate: gameCreate): Response<ResponseBody>

    @PUT("/api/v1/users/game/{id}")
    suspend fun updateGame(@Path("id") userID:Int , @Body gameUpdate: gameUpdate) : Response<ResponseBody>

    @POST("/api/v1/users/playlist")
    suspend fun addPlaylist(@Body addPlaylistData: addPlaylistData): Response<ResponseBody>

    @GET("/api/v1/users/playlist/{id}")
    suspend fun getPlaylist(@Path("id") userID: Int): Response<List<playlistDataItem>>

    @DELETE("/api/v1/users/playlist/{id}")
    suspend fun deletePlaylist(@Path("id") userID: Int): Response<ResponseBody>

    @POST("/api/v1/users/addsongplaylist")
    suspend fun addSong(@Body addSongData: addSongData): Response<ResponseBody>

    @GET("/api/v1/users/playlist/{id}/specific")
    suspend fun getSpecificPlaylist(@Path("id") playlistID: Int): Response<List<playlistDataItem>>

    @GET("api/v1/users/getPlaylistMeta/{id}")
    suspend fun getPlaylistMetadata(@Path("id") playlistID: Int): Response<List<playlistMetadata>>

    @DELETE("/api/v1/users/playlist/{spotifyRef}/song")
    suspend fun deleteSong(@Path("spotifyRef") spotifyRef: String): Response<ResponseBody>

    @PUT("/api/v1/users/playlist/{id}")
    suspend fun updatePlaylist(@Path("id") id: Int, @Body updatePlaylistData: updatePlaylistData): Response<ResponseBody>

    @PUT("/api/v1/users/id/{id}")
    suspend fun updateUsername(@Path("id") id: Int, @Body username: Username): Response<ResponseBody>

    @PUT("/api/v1/users/idImg/{id}")
    suspend fun updateImage(@Path("id") id: Int, @Body imagePath: imagePath): Response<ResponseBody>

    @GET("/api/v1/users/idImg/{id}")
    suspend fun getImage(@Path("id") id: Int): Response<List<imagePathResponse>>

    @GET("/api/v1/users/abouts/s")
    suspend fun getAbout(): Response<List<aboutResponse>>

    @GET("/api/v1/users/feedback/get")
    suspend fun getFeedback(): Response<List<feedbackResponse>>

    @PUT("/api/v1/users/abouts")
    suspend fun updateAbout(@Body aboutResponse: aboutResponse): Response<ResponseBody>

    @POST("/api/v1/users/idImg/{id}")
    suspend fun createImage(@Path("id") id: Int, @Body imagePath: imagePath): Response<ResponseBody>

    @DELETE("/api/v1/users/feedback/{id}")
    suspend fun deleteFeedback(@Path("id") id: Int): Response<ResponseBody>

    @GET("/api/v1/users/query/{id}")
    suspend fun getQuery(@Path("id") id: Int): Response<List<QueryResponse>>

    @POST("/api/v1/users/query")
    suspend fun saveQuery(@Body queryData: queryData): Response<ResponseBody>

    //Musicbrainz API ================================
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
                                   @Query("offset") offset: Int): Response<artistResponse>

    @GET("https://api.spotify.com/v1/search")
    suspend fun searchTrackSpotify(@Header("Authorization") authHeader:String,
                                    @Query("q") q: String,
                                    @Query("type") type: String,
                                    @Query("limit") limit: Int,
                                    @Query("offset") offset: Int): Response<trackResponse>

    @GET("https://api.spotify.com/v1/recommendations")
    suspend fun getRecommendation(@Header("Authorization") authHeader: String,
                                  @Query("limit") limit: Int = 10,
                                  @Query("seed_artists") seed_artists: String,
                                  @Query("seed_tracks") seed_tracks: String): Response<recommendationResponse>

    // ACRCloud API

    @POST("https://identify-eu-west-1.acrcloud.com/v1/identify")
    suspend fun identiftSong(@Field("sample") sample: Objects,
                             @Field("access_key") access_key: String,
                             @Field("sample_bytes") sampel_bytes: Int,
                             @Field("timestamp") timestamp: String,
                             @Field("signature") signature: String,
                             @Field("data_type") data_type: String,
                             @Field("signature_version") signature_version: Int = 1)

    @GET("https://api.spotify.com/v1/albums/{id}")
    suspend fun getAlbum(@Header("Authorization") authHeader: String, @Path("id") albumID: String): Response<getAlbumResponse>

    @GET("https://api.spotify.com/v1/tracks/{id}")
    suspend fun getTrack(@Header("Authorization") authHeader: String, @Path("id") trackID: String): Response<getTrackResponse>

    @GET("https://api.spotify.com/v1/artists/{id}")
    suspend fun getArtist(@Header("Authorization") authHeader: String, @Path("id") trackID: String): Response<getArtistResponse>

    @GET("https://api.spotify.com/v1/artists/{id}/top-tracks")
    suspend fun getTopTracks(@Header("Authorization") authHeader: String, @Path("id") trackID: String, @Query("market") market: String = "US"): Response<getTopTrackResponse>

    @GET("https://api.spotify.com/v1/artists/{id}/albums")
    suspend fun getArtistAlbum(@Header("Authorization") authHeader: String, @Path("id") trackID: String): Response<getArtistAlbumResponse>

    @GET("https://api.spotify.com/v1/artists/{id}/related-artists")
    suspend fun getRelatedArtist(@Header("Authorization") authHeader: String, @Path("id") trackID: String): Response<relatedArtistResponse>

    @GET("https://api.spotify.com/v1/albums/{id}/tracks")
    suspend fun getAlbumTrack(@Header("Authorization") authHeader: String, @Path("id") trackID: String): Response<getAlbumTrackResponse>

    @GET("https://api.spotify.com/v1/tracks")
    suspend fun getSeveralTracks(@Header("Authorization") authHeader: String, @Query("ids") q: String): Response<getSeveralTrackResponse>

}


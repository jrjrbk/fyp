package com.example.testsem7v1.interfaces

interface spotifyItemClickListener {
    //Take the ID of the recommendation response.
    fun passTrackRef(id: String)

    fun passAlbumRef(id: String)

    fun passArtistRef(id: String){
        //default
    }
}
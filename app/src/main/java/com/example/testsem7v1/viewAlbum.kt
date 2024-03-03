package com.example.testsem7v1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.activity.accessToken
import com.example.testsem7v1.adapter.artistAlbumAdapter
import com.example.testsem7v1.adapter.itemAlbumAdapter
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.spotify.albumTrack.getAlbumTrackResponse
import com.example.testsem7v1.retrofit.spotify.getAlbum.getAlbumResponse
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException


class viewAlbum : Fragment() {

    // RecyclerView
    private lateinit var itemAlbumRV: RecyclerView
    private lateinit var itemAlbumAdapter: itemAlbumAdapter

    private lateinit var albumTrackData: getAlbumTrackResponse
    private lateinit var albumData: getAlbumResponse



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_album, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var albumID = requireArguments().getString("albumRef")
        initItemAlbumRV()

        val albumImage: ImageView = requireView().findViewById(R.id.albumItemImage)
        val albumName: TextView = requireView().findViewById(R.id.albumItemName)
        val albumArtistName: TextView = requireView().findViewById(R.id.albumArtistName)
        albumImage.clipToOutline=true


        lifecycleScope.launch {
            getAlbum(albumID.toString())
            getAlbumTrack(albumID.toString())

            Picasso.get().load(albumData.images[1].url).into(albumImage)
            albumName.text = albumData.name
            albumArtistName.text = "${albumData.name}●${albumData.release_date.subSequence(0,4)}"
        }
    }


    private suspend fun getAlbumTrack(id:String):Boolean {
        val response = try {
            val authentication = "${accessToken.token_type} ${accessToken.access_token}"
            retrofitInstance.api.getAlbumTrack(authentication,id)
        } catch (e: IOException) {
            Log.e("PLAYLIST-GETGOOD", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLIST-GETGOOD", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            albumTrackData = response.body()!!
            itemAlbumAdapter.itemAlbum = albumTrackData.items
        }
        return true
    }

    private suspend fun getAlbum(id: String):Boolean {
        val response = try {
            val authentication = "${accessToken.token_type} ${accessToken.access_token}"
            retrofitInstance.api.getAlbum(authentication,id)
        } catch (e: IOException) {
            Log.e("PLAYLIST-GETGOOD", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLIST-GETGOOD", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            albumData = response.body()!!
        }
        return true
    }

    private fun initItemAlbumRV(){
        itemAlbumRV = requireView().findViewById(R.id.albumSongItemRV)
        itemAlbumAdapter = itemAlbumAdapter()
        itemAlbumRV.adapter = itemAlbumAdapter
        itemAlbumRV.layoutManager = LinearLayoutManager(context)
    }



}
package com.example.testsem7v1.popup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.R
import com.example.testsem7v1.activity.userID
import com.example.testsem7v1.adapter.mainPlaylistAdapter
import com.example.testsem7v1.adapter.playlistPopupAdapter
import com.example.testsem7v1.interfaces.OnPlaylistItemClickListener
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.systemDatabase.addSongData
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class addToPlaylistPopup: DialogFragment(), OnPlaylistItemClickListener {

    //RecyclerView
    private lateinit var playlistPopupRV: RecyclerView
    private lateinit var playlistPopupAdapter: playlistPopupAdapter

    private  var spotifyRef = ""
    private var artistRef = ""
    override fun onStart() {
        super.onStart()
        val width = 640
        val height = 640
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setGravity(Gravity.CENTER)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.popup_add_playlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPlaylistRV()

        spotifyRef = requireArguments().getString("SpotifyRef").toString()
        artistRef  = requireArguments().getString("ArtistRef").toString()

        lifecycleScope.launch {
            getPlaylistInfo()
        }

    }

    private suspend fun getPlaylistInfo():Boolean {
        var id = userID.accountID
        val response = try {
            retrofitInstance.api.getPlaylist(id)
        } catch (e: IOException) {
            Log.e("PLAYLIST-GETGOOD", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLIST-GETGOOD", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            playlistPopupAdapter.mainPlaylist = response.body()!!
        }

        return true
    }

    override fun passData(data: Int) {
        Log.e("ADDPLAYLIST", data.toString())
        //playlistID, SpotifyRef

        //function Call
        //add playlistID and spotifyRef to database
        lifecycleScope.launch {
            addSongToPlaylist(data,spotifyRef,artistRef)
            dismiss()
            Toast.makeText(context,"Song added!", Toast.LENGTH_SHORT).show()

        }

    }


    private suspend fun addSongToPlaylist(playlistID: Int, spotifyRef: String, artistRef: String) : Boolean{
        val addData = addSongData(playlistID, spotifyRef, artistRef)
        val response = try {
            retrofitInstance.api.addSong(addData)
        } catch (e: IOException) {
            Log.e("PLAYLIST-GETGOOD", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLIST-GETGOOD", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            Log.e("AddPlaylist", response.body().toString())

        }

        return true    }
    fun initPlaylistRV(){
        playlistPopupAdapter = playlistPopupAdapter()
        playlistPopupAdapter.listener=this
        playlistPopupRV = requireView().findViewById(R.id.playlistPopupRV)
        playlistPopupRV.adapter = playlistPopupAdapter
        playlistPopupRV.layoutManager = LinearLayoutManager(context)
    }
}
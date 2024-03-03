package com.example.testsem7v1

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.activity.accessToken
import com.example.testsem7v1.activity.backPressedCallback
import com.example.testsem7v1.adapter.itemPlaylistAdapter
import com.example.testsem7v1.interfaces.onDeleteSongClickListener
import com.example.testsem7v1.interfaces.onDoneListener
import com.example.testsem7v1.popup.clickOptionPopUp
import com.example.testsem7v1.popup.updatePlaylistPopup
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.spotify.getSeveralTrack.getSeveralTrackResponse
import com.example.testsem7v1.retrofit.spotify.recommendation.recommendationResponse
import com.example.testsem7v1.retrofit.systemDatabase.playlistDataItem
import com.example.testsem7v1.retrofit.systemDatabase.playlistMetadata
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Paths


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [playlistItemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class playlistItemFragment : Fragment(), onDeleteSongClickListener, onDoneListener {
    private lateinit var storage: Storage


    // RecyclerView - for getting data from callBack
    private lateinit var playlistItemAdapter: itemPlaylistAdapter
    private lateinit var playlistitemRV: RecyclerView


    private lateinit var playlistData: List<playlistDataItem>
    private lateinit var playlistMetadata: List<playlistMetadata>
    private lateinit var getSeveralTrackData: getSeveralTrackResponse
    private lateinit var recommendationResponse: recommendationResponse

    //POPUP PASS
    private var playlistID = ""

    //
    private lateinit var playlistName: TextView
    private lateinit var playlistImage: ImageView
    private lateinit var editPlaylist: Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist_item, container, false)

    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPlaylistItemRV()
        playlistName = requireView().findViewById(R.id.playlistItemName)
        playlistImage = requireView().findViewById(R.id.playlistItemImg)
        editPlaylist = requireView().findViewById(R.id.editPlaylist)
        val enhancePlaylist = requireView().findViewById<Button>(R.id.enhanceButton)

        playlistImage.clipToOutline=true
        playlistID = requireArguments().getString("playlistID").toString()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, backPressedCallback)

        //TEST GOOGLE API
        val credentialsInputStream: InputStream = requireContext().resources.openRawResource(R.raw.credential)
        val storageOptions = StorageOptions.newBuilder()
            .setCredentials(ServiceAccountCredentials.fromStream(credentialsInputStream))
            .build()
        storage = storageOptions.service

        //edit playlist
        //enhance playlist
        //dlm recyclerview
        lifecycleScope.launch {
            getPlaylistData(playlistID.toInt()) // To get name and playlist
            playlistName.text = playlistData[0].playlistName
            Picasso.get().load(playlistData[0].playlistImage).into(playlistImage)

            getPlaylistMetaData(playlistID.toInt()) // To get its metadata
            if(playlistMetadata.isNotEmpty()) {
                Log.e("PLAYLISTMETA", playlistMetadata[0].playlistSongRef)
            }

            //iterate and add all spotify ref from response to string
            var spotifyRef = playlistMetadata.map{it.playlistSongRef}


            getTrackData(spotifyRef.joinToString(",")) // To convert the ref to actual data

            editPlaylist.setOnClickListener {
//                openGallery()
                //PopUP
                showPopup()
            }

            enhancePlaylist.setOnClickListener {
                //get recommendations
                //populate it
                val spotifySongRef = playlistMetadata.shuffled().take(5).map { it.playlistSongRef }
//                val spotifyArtistRef = playlistMetadata.shuffled().take(5).map { it.playlistArtistRef }


                lifecycleScope.launch {
                    getRecommendations("",spotifySongRef.joinToString(","),playlistMetadata.size)
                    playlistItemAdapter.RefreshRec(recommendationResponse.tracks)
                }
            }
        }



    }

    fun showPopup(){
        val popup = updatePlaylistPopup()
        val bundle = Bundle()
        bundle.putString("playlistID",playlistID)
        popup.arguments = bundle
        popup.isCancelable = false
        popup.listener = this
        popup.show(requireActivity().supportFragmentManager, "updateOption")
    }

    private suspend fun getPlaylistMetaData(playlistID: Int): Boolean {
        val response = try {
            retrofitInstance.api.getPlaylistMetadata(playlistID)
        } catch (e: IOException) {
            Log.e("PLAYLISTITEM", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLISTITEM", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            playlistMetadata = response.body()!!
        }
        return true
    }

    private suspend fun getPlaylistData(playlistID: Int):Boolean {
        val response = try {
            retrofitInstance.api.getSpecificPlaylist(playlistID)
        } catch (e: IOException) {
            Log.e("PLAYLISTITEM", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLISTITEM", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            playlistData = response.body()!!
        }
        return true
    }

    private suspend fun getTrackData(spotifyRef: String):Boolean {
        val response = try {
            val authentication = "${accessToken.token_type} ${accessToken.access_token}"
            retrofitInstance.api.getSeveralTracks(authentication,spotifyRef)
        } catch (e: IOException) {
            Log.e("PLAYLISTITEM", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLISTITEM", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            getSeveralTrackData = response.body()!!
            playlistItemAdapter.trackItem = getSeveralTrackData.tracks
        }
        return true
    }

    //needs the seeds of artist and tracks. Limited to 5.

    private suspend fun getRecommendations(spotifyRefArtist: String,spotifyRefTrack: String, playlistItemCount: Int): Boolean {

        var itemCount = playlistItemCount
        // Recommendation Item Count Check, minimum = 5, maximum = 10
        if(playlistItemCount < 5){
            itemCount = 5
        }
        else if(playlistItemCount >10){
            itemCount = 5
        }

        val response = try {
            val authentication = "${accessToken.token_type} ${accessToken.access_token}"
            retrofitInstance.api.getRecommendation(authentication, limit = itemCount,spotifyRefArtist,spotifyRefTrack)
        } catch (e: IOException) {
            Log.e("PLAYLISTITEM", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLISTITEM", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            recommendationResponse = response.body()!!
            playlistItemAdapter.recItem = recommendationResponse.tracks
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        backPressedCallback.remove()
    }

    private fun initPlaylistItemRV(){
        playlistitemRV = requireView().findViewById(R.id.playlistItemRV)
        playlistItemAdapter = itemPlaylistAdapter()
        playlistitemRV.adapter = playlistItemAdapter
        playlistitemRV.layoutManager = LinearLayoutManager(context)
        playlistItemAdapter.listener = this
    }

    override fun passData(data: String) {
        // get the spotify String.
        lifecycleScope.launch {
            Log.e("PASSDATA", "SUCCESSFULLY PASSED DATA: $data")
            getPlaylistMetaData(playlistData[0].playlistID)
            var spotifyRef = playlistMetadata.map{it.playlistSongRef}
            getTrackData(spotifyRef.joinToString(","))
            playlistItemAdapter.Refresh(getSeveralTrackData.tracks)
        }
    }

    override fun done() {
        lifecycleScope.launch {
            getPlaylistData(playlistID.toInt())
            playlistName.text = playlistData[0].playlistName
            Picasso.get().load(playlistData[0].playlistImage).into(playlistImage)
        }
    }


}
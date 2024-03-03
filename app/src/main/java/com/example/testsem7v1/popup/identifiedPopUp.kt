package com.example.testsem7v1.popup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.testsem7v1.activity.ACRResponse
import com.example.testsem7v1.R
import com.example.testsem7v1.activity.accessToken
import com.example.testsem7v1.retrofit.retrofitInstance
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class identifiedPopUp : DialogFragment() {

    override fun onStart() {
        super.onStart()
        val width = 450
        val height = 500
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setGravity(Gravity.CENTER)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_identified, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val title: TextView = requireView().findViewById(R.id.identifiedTitle)
        val artist: TextView = requireView().findViewById(R.id.identifiedArtist)
        val image: ImageView = requireView().findViewById(R.id.identifiedImage)

        title.text = ACRResponse.metadata.music[0].title
        artist.text = ACRResponse.metadata.music[0].artists[0].name

        var spotifyid =
            ACRResponse.metadata.music[0].external_metadata.spotify.album.id // for picture

        Log.e("POPUPFRAGMENT", spotifyid)

        lifecycleScope.launch {
            val response = try {
                retrofitInstance.api.getAlbum(
                    authHeader = "${accessToken.token_type} ${accessToken.access_token}",
                    spotifyid
                )
            } catch (e: IOException) {
                Log.e("POPUP", "IOException, you might not have internet connection")
                return@launch
            } catch (e: HttpException) {
                Log.e("POPUP", "HttpException, unexpected response")
                return@launch
            }

            if(response.isSuccessful && response.body() != null){
                val getAlbumResponse = response.body()
                val identifiedImage: ImageView = view.findViewById(R.id.identifiedImage)
                Log.e("POPUP", "ada kah image ${getAlbumResponse!!.images[0].url}")
                Picasso.get().load(getAlbumResponse!!.images[0].url).into(identifiedImage)
            }
        }



    }


    suspend fun getAlbumInfo() {
        var spotifyid =
            ACRResponse.metadata.music[0].external_metadata.spotify.album.id // for picture
    }
}



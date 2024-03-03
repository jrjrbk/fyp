package com.example.testsem7v1.popup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.testsem7v1.R
import com.example.testsem7v1.activity.accessToken
import com.example.testsem7v1.activity.loginSession
import com.example.testsem7v1.interfaces.optionItemClickListener
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.spotify.getAlbum.getAlbumResponse
import com.example.testsem7v1.retrofit.spotify.getTrack.getTrackResponse
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException


/**
 * A simple [Fragment] subclass.
 * Use the [clickOptionPopUp.newInstance] factory method to
 * create an instance of this fragment.
 */
class clickOptionPopUp : DialogFragment() {

    private lateinit var trackData: getTrackResponse
    private lateinit var albumData: getAlbumResponse

    lateinit var listener: optionItemClickListener
    override fun onStart() {
        super.onStart()
        val width = 500
        val height = 540
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setGravity(Gravity.CENTER)
//        dialog?.window?.setDimAmount(0.0F)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.popup_click_option, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spotifyRef = requireArguments().getString("SpotifyRef")
        val clickType = requireArguments().getString("Type")


        val itemName: TextView = requireView().findViewById(R.id.updateOptionTV)
        val artistName: TextView = requireView().findViewById(R.id.updateOptionChangeImage)
        val itemImage: ImageView = requireView().findViewById(R.id.updateOptionPopupImage)
        //Cards
        val viewArtist: CardView = requireView().findViewById(R.id.viewArtistCard)
        val viewInfo: CardView = requireView().findViewById(R.id.viewInfoCard)
        val addToPlaylist: CardView = requireView().findViewById(R.id.addToPlaylistCard)


        addToPlaylist.isVisible = loginSession


        if(clickType == "Track"){
            viewInfo.isVisible = false

            lifecycleScope.launch {
                getTrackInfo(spotifyRef.toString())
                itemName.text = trackData.name
                artistName.text = trackData.artists[0].name
                Picasso.get().load(trackData.album.images[1].url).into(itemImage)

                viewArtist.setOnClickListener{
                    listener.viewArtistData(trackData.artists[0].id)
                    dismiss()
                }
            }
        }

        else if(clickType == "TrackArtist"){
            viewInfo.isVisible = false
            viewArtist.isVisible = false

            lifecycleScope.launch {
                getTrackInfo(spotifyRef.toString())
                itemName.text = trackData.name
                artistName.text = trackData.artists[0].name
                Picasso.get().load(trackData.album.images[1].url).into(itemImage)

                viewArtist.setOnClickListener{
                    listener.viewArtistData(trackData.artists[0].id)
                    dismiss()
                }
            }
        }
        else if(clickType == "AlbumArtist"){
            viewArtist.isVisible = false
            addToPlaylist.isVisible = false

            lifecycleScope.launch {
                getAlbumInfo(spotifyRef.toString())
                itemName.text = albumData.name
                artistName.text = albumData.artists[0].name
                Picasso.get().load(albumData.images[1].url).into(itemImage)

                viewInfo.setOnClickListener{
                    listener.viewAlbumData(albumData.id)
                    dismiss()
                }
            }
        }

        else if(clickType == "Album"){
            addToPlaylist.isVisible = false

            lifecycleScope.launch {
                getAlbumInfo(spotifyRef.toString())
                itemName.text = albumData.name
                artistName.text = albumData.artists[0].name
                Picasso.get().load(albumData.images[1].url).into(itemImage)

                viewArtist.setOnClickListener{
                    listener.viewArtistData(albumData.artists[0].id)
                    dismiss()
                }

                viewInfo.setOnClickListener{
                    listener.viewAlbumData(albumData.id)
                    dismiss()
                }
            }
        }

        addToPlaylist.setOnClickListener {
            Log.e("passTrackRef","ID pressed: $id")
            val popup = addToPlaylistPopup()
            val bundle = Bundle()
            bundle.putString("SpotifyRef",trackData.id)
            bundle.putString("ArtistRef", trackData.artists[0].id)
            popup.arguments = bundle
//            popup.listener = this
            popup.show(requireActivity().supportFragmentManager, "add")
            dismiss()
        }

    }

    private suspend fun getTrackInfo(id: String):Boolean{

            val response = try {
                val authentication = "${accessToken.token_type} ${accessToken.access_token}"
                retrofitInstance.api.getTrack(authentication,id)
            } catch (e: IOException) {
                Log.e("PLAYLIST-GETGOOD", "IOException, you might not have internet connection")
                return false

            } catch (e: HttpException) {
                Log.e("PLAYLIST-GETGOOD", "HttpException, unexpected response")
                return false
            }
            if (response.isSuccessful && response.body() != null) {
                trackData = response.body()!!
            }

            return true
    }

    private suspend fun getAlbumInfo(id: String):Boolean{

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




}
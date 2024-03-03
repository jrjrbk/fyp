package com.example.testsem7v1

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.activity.accessToken
import com.example.testsem7v1.activity.backPressedCallback
import com.example.testsem7v1.adapter.artistAlbumAdapter
import com.example.testsem7v1.adapter.artistRelatedAdapter
import com.example.testsem7v1.adapter.homeMusicRVAdapter
import com.example.testsem7v1.adapter.topTrackAdapter
import com.example.testsem7v1.interfaces.optionItemClickListener
import com.example.testsem7v1.interfaces.spotifyItemClickListener
import com.example.testsem7v1.popup.clickOptionPopUp
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.spotify.getArtist.getArtistResponse
import com.example.testsem7v1.retrofit.spotify.getTopTrack.getTopTrackResponse
import com.example.testsem7v1.retrofit.spotify.relatedArtist.relatedArtistResponse
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import java.text.NumberFormat
import java.util.Locale


class aboutArtistFragment : Fragment(), spotifyItemClickListener,optionItemClickListener {

    lateinit var artistData: getArtistResponse
    lateinit var topTrackData: getTopTrackResponse
    lateinit var relatedArtistData: relatedArtistResponse

    //RecyclerView
    private lateinit var aboutTopTrackRV: RecyclerView
    private lateinit var aboutTopTrackAdapter: topTrackAdapter

    private lateinit var aboutAlbumRV: RecyclerView
    private lateinit var aboutAlbumAdapter: artistAlbumAdapter

    private lateinit var relatedArtistRV:RecyclerView
    private lateinit var relatedArtistAdapter: artistRelatedAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about_artist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var bundle = requireArguments().getString("artistRef")
        initTopTrackAdapter()
        initAlbumAdapter()
        initArtistAdapter()

        val artistImage: ImageView = requireView().findViewById(R.id.aboutArtistImage)
        val artistName: TextView = requireView().findViewById(R.id.aboutArtistName)
        val follower: TextView = requireView().findViewById(R.id.spotifyFollower)
        val spotifyButton: ImageView = requireView().findViewById(R.id.spotifyLink)
        val genreList: TextView = requireView().findViewById(R.id.aboutGenreListTV)



        lifecycleScope.launch {
            getArtistInfo(bundle.toString())

            // Yea
            Picasso.get().load(artistData.images[0].url).into(artistImage)
            artistName.text = artistData.name
            follower.text = NumberFormat.getNumberInstance(Locale.getDefault()).format(artistData.followers.total)
            val stringList = artistData.genres
            genreList.text = stringList.joinToString()

            getTopTrack(bundle.toString())
            getArtistAlbum(bundle.toString())
            getRelatedArtist(bundle.toString())

            spotifyButton.setOnClickListener {
                val Uri = Uri.parse(artistData.uri)
                val intent = Intent(Intent.ACTION_VIEW, Uri)
                startActivity(intent)
            }
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        backPressedCallback.remove()

    }

    private suspend fun getArtistInfo(id: String):Boolean{

        val response = try {
            val authentication = "${accessToken.token_type} ${accessToken.access_token}"
            retrofitInstance.api.getArtist(authentication,id)
        } catch (e: IOException) {
            Log.e("PLAYLIST-GETGOOD", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLIST-GETGOOD", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            artistData = response.body()!!
        }

        return true
    }

    private suspend fun getTopTrack(id: String):Boolean{

        val response = try {
            val authentication = "${accessToken.token_type} ${accessToken.access_token}"
            retrofitInstance.api.getTopTracks(authentication,id)
        } catch (e: IOException) {
            Log.e("PLAYLIST-GETGOOD", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLIST-GETGOOD", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            topTrackData = response.body()!!
            aboutTopTrackAdapter.topTrack = topTrackData.tracks
        }

        return true
    }

    private suspend fun getRelatedArtist(id: String): Boolean {
        val response = try {
            val authentication = "${accessToken.token_type} ${accessToken.access_token}"
            retrofitInstance.api.getRelatedArtist(authentication,id)
        } catch (e: IOException) {
            Log.e("PLAYLIST-GETGOOD", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLIST-GETGOOD", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            relatedArtistData = response.body()!!
            relatedArtistAdapter.relatedArtist = relatedArtistData.artists
        }

        return true
    }

    private suspend fun getArtistAlbum(id: String): Boolean {
        val response = try {
            val authentication = "${accessToken.token_type} ${accessToken.access_token}"
            retrofitInstance.api.getArtistAlbum(authentication,id)
        } catch (e: IOException) {
            Log.e("PLAYLIST-GETGOOD", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLIST-GETGOOD", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            var item = response.body()!!
            aboutAlbumAdapter.artistAlbum = item.items
        }

        return true
    }

    fun initTopTrackAdapter(){
        // Adapter for Music
        aboutTopTrackRV = requireView().findViewById(R.id.aboutTopTrackRV)
        aboutTopTrackAdapter = topTrackAdapter()
        aboutTopTrackRV.adapter = aboutTopTrackAdapter
        aboutTopTrackRV.layoutManager =
            GridLayoutManager(this@aboutArtistFragment.context, 1, GridLayoutManager.HORIZONTAL, false)

        aboutTopTrackAdapter.listener = this
    }

    fun initAlbumAdapter(){
        aboutAlbumRV = requireView().findViewById(R.id.aboutAlbumRV)
        aboutAlbumAdapter = artistAlbumAdapter()
        aboutAlbumRV.adapter = aboutAlbumAdapter
        aboutAlbumRV.layoutManager =
            GridLayoutManager(this@aboutArtistFragment.context, 1, GridLayoutManager.HORIZONTAL, false)

        aboutAlbumAdapter.listener = this
    }

    fun initArtistAdapter(){
        relatedArtistRV = requireView().findViewById(R.id.relatedArtistRV)
        relatedArtistAdapter = artistRelatedAdapter()
        relatedArtistRV.adapter = relatedArtistAdapter
        relatedArtistRV.layoutManager =
            GridLayoutManager(this@aboutArtistFragment.context, 1, GridLayoutManager.HORIZONTAL, false)
        relatedArtistAdapter.listener = this
    }

    override fun passTrackRef(id: String) {
        Log.e("passTrackRef","ID pressed: $id")
        val popup = clickOptionPopUp()
        val bundle = Bundle()
        bundle.putString("SpotifyRef",id)
        bundle.putString("Type","TrackArtist")
        popup.arguments = bundle
        popup.listener = this
        popup.show(requireActivity().supportFragmentManager, "option")

    }

    override fun passAlbumRef(id: String) {
        Log.e("passAlbumRef","ID pressed: $id")
        val popup = clickOptionPopUp()
        val bundle = Bundle()
        bundle.putString("SpotifyRef",id)
        bundle.putString("Type","AlbumArtist")
        popup.arguments = bundle
        popup.listener = this
        popup.show(requireActivity().supportFragmentManager, "option")
    }

    override fun passArtistRef(id: String) {
        super.passArtistRef(id)
        val aboutArtistFragment = aboutArtistFragment()
        val bundle = Bundle()
        bundle.putString("artistRef",id)
        aboutArtistFragment.arguments = bundle

        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, aboutArtistFragment).addToBackStack("artist").commit()
    }

    override fun viewAlbumData(id: String) {
        super.viewAlbumData(id)

        val viewAlbum = viewAlbum()
        val bundle = Bundle()
        bundle.putString("albumRef",id)
        viewAlbum.arguments = bundle

        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, viewAlbum).addToBackStack("album").commit()
    }
}
package com.example.testsem7v1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.adapter.homeAlbumRVAdapter
import com.example.testsem7v1.databinding.FragmentHomeBinding
import com.example.testsem7v1.adapter.homeMusicRVAdapter
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.spotify.recommendation.recommendationResponse
import retrofit2.HttpException
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

//        homervAdapter
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lateinit var musicRecyclerAdapter: homeMusicRVAdapter
        lateinit var musicRVHome: RecyclerView
        lateinit var albumRecyclerAdapter: homeAlbumRVAdapter
        lateinit var albumRVHome: RecyclerView

        //Progress bar
        var musicProgressbar: ProgressBar = view.findViewById(R.id.musicProgressBar)
        var albumProgressbar: ProgressBar = view.findViewById(R.id.albumProgressBar)


        // Adapter for Music
        musicRVHome = view.findViewById(R.id.homeMusicRV)
        musicRecyclerAdapter = homeMusicRVAdapter()
        musicRVHome.adapter = musicRecyclerAdapter
        musicRVHome.layoutManager =
            GridLayoutManager(this@HomeFragment.context, 1, GridLayoutManager.HORIZONTAL, false)

        // Adapter for Album
        albumRVHome = view.findViewById(R.id.homeAlbumRV)
        albumRecyclerAdapter = homeAlbumRVAdapter()
        albumRVHome.adapter = albumRecyclerAdapter
        albumRVHome.layoutManager =
            GridLayoutManager(this@HomeFragment.context, 1, GridLayoutManager.HORIZONTAL, false)


        // Token & RecyclerView populate
        lifecycleScope.launchWhenCreated {
            musicProgressbar.isVisible = true
            albumProgressbar.isVisible = true
            val response = try {
                retrofitInstance.api.getToken(
                    "client_credentials",
                    "20d283513f3b4ea79bb4d0d35b975762",
                    "02fb3bee5b6a4f6598fcc0c28f9a0ce2"
                )
            } catch (e: IOException) {
                Log.e("MainActivity", "IOException, you might not have internet connection")
                Log.e("Error", throw (e))
                return@launchWhenCreated // so that the thread can resume.
            } catch (e: HttpException) {
                Log.e("MainActivity", "HttpException, unexpected response")
                return@launchWhenCreated // so that the thread can resume.
            }

            if (response.isSuccessful && response.body() != null) {
                Log.e("ActivityMain", "Response successful CODE:${response.code()}")
                accessToken = response.body()!!
                Log.e("ActivityMain-ACCESSTOKENTEST", accessToken.access_token)

                // Get the recommendation data for music ================================
                val recommendationResponse = try {
                    val authentication = "${accessToken.token_type} ${accessToken.access_token}"
                    Log.e("TESTAUTHTEST", authentication)
                    retrofitInstance.api.getRecommendation(
                        authentication,
                        5,
                        "4NHQUGzhtTLFvgF5SZesLK",
                        "0c6xIDDpzE81m2q797ordA"
                    )
                } catch (e: IOException) {
                    Log.e("MainActivity", "IOException, you might not have internet connection")
                    Log.e("Error", throw (e))
                    return@launchWhenCreated // so that the thread can resume.
                } catch (e: HttpException) {
                    Log.e("MainActivity", "HttpException, unexpected response")
                    return@launchWhenCreated // so that the thread can resume.
                }
                if (recommendationResponse.isSuccessful && recommendationResponse.body() != null) {
                    Log.e(
                        "ActivityMain",
                        "Response successful CODE:${recommendationResponse.code()}"
                    )
                    var recommendationData: recommendationResponse = recommendationResponse.body()!!

//                    val copyArtistData = listOf(artistData, artistData,artistData,artistData)


                    musicRecyclerAdapter.recommendationTrack = recommendationData.tracks
                    musicProgressbar.isVisible = false
                } else {
                    Log.e("ActivityMain", "Response not successful")
                }

                // Get the recommendation data for album ========================
                val recommendationResponse2 = try {
                    val authentication = "${accessToken.token_type} ${accessToken.access_token}"
                    Log.e("TESTAUTHTEST", authentication)
                    retrofitInstance.api.getRecommendation(
                        authentication,
                        5,
                        "4NHQUGzhtTLFvgF5SZesLK",
                        "0c6xIDDpzE81m2q797ordA"
                    )
                } catch (e: IOException) {
                    Log.e("MainActivity", "IOException, you might not have internet connection")
                    Log.e("Error", throw (e))
                    return@launchWhenCreated // so that the thread can resume.
                } catch (e: HttpException) {
                    Log.e("MainActivity", "HttpException, unexpected response")
                    return@launchWhenCreated // so that the thread can resume.
                }
                if (recommendationResponse2.isSuccessful && recommendationResponse2.body() != null) {
                    Log.e(
                        "ActivityMain",
                        "Response successful CODE:${recommendationResponse2.code()}"
                    )
                    var recommendationData2: recommendationResponse = recommendationResponse2.body()!!

//                    val copyArtistData = listOf(artistData, artistData,artistData,artistData)


                    albumRecyclerAdapter.recommendationAlbum = recommendationData2.tracks
                    albumProgressbar.isVisible = false
                } else {
                    Log.e("ActivityMain", "Response not successful")
                }


            } else {
                Log.e("ActivityMain", "Token retrieval not successful")
            }
        }
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

//    private fun setupRecyclerView() = binding.homeRV.apply {
//        homervAdapter = homervAdapter()
//        adapter = homervAdapter
//        layoutManager = LinearLayoutManager(this.context)
//    }
}
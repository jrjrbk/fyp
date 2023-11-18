package com.example.testsem7v1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.adapter.homeAlbumRVAdapter
import com.example.testsem7v1.databinding.FragmentHomeBinding
import com.example.testsem7v1.adapter.homeMusicRVAdapter
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.spotify.recommendation.recommendationResponse
import com.example.testsem7v1.retrofit.spotify.sAccessToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
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


    // put here so it doesnt run again.
//    lateinit var musicRecyclerAdapter: homeMusicRVAdapter
     var musicRecyclerAdapter =homeMusicRVAdapter()
    lateinit var musicRVHome: RecyclerView
//    lateinit var albumRecyclerAdapter: homeAlbumRVAdapter
     var albumRecyclerAdapter = homeAlbumRVAdapter()
    lateinit var albumRVHome: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        Log.e("START","oncreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onStart() {
        super.onStart()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Progress bar
        val musicProgressbar: ProgressBar = view.findViewById(R.id.musicProgressBar)
        val albumProgressbar: ProgressBar = view.findViewById(R.id.albumProgressBar)


        // Adapter for Music
        musicRVHome = view.findViewById(R.id.homeMusicRV)
//        musicRecyclerAdapter = homeMusicRVAdapter()
        musicRVHome.adapter = musicRecyclerAdapter
        musicRVHome.layoutManager =
            GridLayoutManager(this@HomeFragment.context, 1, GridLayoutManager.HORIZONTAL, false)

        // Adapter for Album
        albumRVHome = view.findViewById(R.id.homeAlbumRV)
//        albumRecyclerAdapter = homeAlbumRVAdapter()
        albumRVHome.adapter = albumRecyclerAdapter
        albumRVHome.layoutManager =
            GridLayoutManager(this@HomeFragment.context, 1, GridLayoutManager.HORIZONTAL, false)


        // How to stop refreshing every time I move on this page?
        // 1. using time (but, what if I exit out the app?)
        // 2. then make a checklist if app is already opened aka data is populated.

        Log.e("HomeFragment", "Is page Loaded? : $isPageLoaded")

        // Token & RecyclerView populate
        lifecycleScope.launchWhenCreated {
            if(musicRecyclerAdapter.recommendationTrack.isNotEmpty()){
                musicProgressbar.isVisible = false
            }
            else {
                musicProgressbar.isVisible = true
                albumProgressbar.isVisible = true
            }

            var isValidToken = isTokenValid()
            Log.e("HomeFragment", "Is Token Valid?: $isValidToken")
            //ms/1000 = s , s/60 = minutes


            var requestTime = (getRequestTime()/1000)/60 //in minutes

            Log.e("HomeFragment", "Request exceeded time: $requestTime minute")
            // only refresh if more than 10 minutes or if !isPageLoaded aka Application Restart
            if(isValidToken  && requestTime > 10 ){
                Log.e("HomeFragment", "Token is Valid!")
                getStoredToken()
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
                    Log.e("ADAPTERSIZE3", musicRecyclerAdapter.recommendationTrack.size.toString())

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
                    var recommendationData2: recommendationResponse =
                        recommendationResponse2.body()!!

//                    val copyArtistData = listOf(artistData, artistData,artistData,artistData)


                    albumRecyclerAdapter.recommendationAlbum = recommendationData2.tracks
                    albumProgressbar.isVisible = false
                    isPageLoaded=saveRequestTime()
                } else {
                    Log.e("ActivityMain", "Response not successful")
                }

            }

            else if(!isValidToken && requestTime > 10 ){
                Log.e("HomeFragment", "Token is not valid!")
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
                    Log.e("HomeFragmentAccessToken", accessToken.access_token)

                    saveToken(accessToken)

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
                        var recommendationData2: recommendationResponse =
                            recommendationResponse2.body()!!

//                    val copyArtistData = listOf(artistData, artistData,artistData,artistData)


                        albumRecyclerAdapter.recommendationAlbum = recommendationData2.tracks
                        albumProgressbar.isVisible = false
                        isPageLoaded=saveRequestTime()
                    } else {
                        Log.e("ActivityMain", "Response not successful")
                    }


                } else {
                    Log.e("ActivityMain", "Token retrieval not successful")
                }
            }

            else if(!isPageLoaded){
                Log.e("HomeFragment", "Token is not valid!")
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
                    Log.e("HomeFragmentAccessToken", accessToken.access_token)

                    saveToken(accessToken)

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
                        var recommendationData2: recommendationResponse =
                            recommendationResponse2.body()!!

//                    val copyArtistData = listOf(artistData, artistData,artistData,artistData)


                        albumRecyclerAdapter.recommendationAlbum = recommendationData2.tracks
                        albumProgressbar.isVisible = false
                        isPageLoaded=saveRequestTime()
                    } else {
                        Log.e("ActivityMain", "Response not successful")
                    }


                } else {
                    Log.e("ActivityMain", "Token retrieval not successful")
                }
            }



        }
        Log.e("ADAPTERSIZE2", musicRecyclerAdapter.recommendationTrack.size.toString())

    }


    private suspend fun isTokenValid(): Boolean {

        val current_time = System.currentTimeMillis()
        // datastore.data retrieves the value in dataStore

        val created_token_time = requireContext().dataStore.data.map {
            //transforms it into an int using .map [] ?: 0,
                accessToken ->
            accessToken[longPreferencesKey("created_timestamp")] ?: null
        }.first()

        if(created_token_time == null){
            return false
        }


        Log.e("HomeFragmentIsTokenValid()", "created_token_time: ${created_token_time!!}")
        val time_exceeded = current_time - created_token_time!!

        Log.e("HomeFragment", "Token Time Exceeded: ${(time_exceeded/1000)/60} minutes")

        // millisecond/1000 = seconds
        if (time_exceeded/1000 > 3600) {
            return false
        }

            return true
    }

    private suspend fun getStoredToken(){
        var access_token= requireContext().dataStore.data.map {
                accessToken -> accessToken[stringPreferencesKey("access_token")] ?: null
        }.first()!!

        var expires_in = requireContext().dataStore.data.map {
                accessToken -> accessToken[intPreferencesKey("expires_in")] ?: null
        }.first()!!

       var token_type = requireContext().dataStore.data.map {
                accessToken -> accessToken[stringPreferencesKey("token_type")] ?: null
        }.first()!!

        val stored_accessToken= sAccessToken(access_token = access_token, expires_in=expires_in,token_type=token_type)
        accessToken = stored_accessToken
    }

    private suspend fun saveToken(token: sAccessToken) {
        requireContext().dataStore.edit { accessToken ->
            accessToken[stringPreferencesKey("access_token")] = token.access_token
            accessToken[intPreferencesKey("expires_in")] = token.expires_in
            accessToken[stringPreferencesKey("token_type")] = token.token_type
            accessToken[longPreferencesKey("created_timestamp")] = System.currentTimeMillis()
            Log.e("HomeFragmentAccessToken", "created_timestamp: ${System.currentTimeMillis()}" )

        }
    }

    private suspend fun saveRequestTime(): Boolean{
        requireContext().dataStore.edit{ accessToken ->
            accessToken[longPreferencesKey("created_request_timestamp")] = System.currentTimeMillis()
        }
        Log.e("HomeFragment","DEBUG HERE")
        return true
    }

    private suspend fun getRequestTime(): Long {

        var timestamp= requireContext().dataStore.data.map {
                accessToken -> accessToken[longPreferencesKey("created_request_timestamp")] ?: null
        }.first()

        if(timestamp == null){
            return 660000
        }

        var exceed_time = System.currentTimeMillis() - timestamp

        return exceed_time
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


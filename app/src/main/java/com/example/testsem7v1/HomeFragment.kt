package com.example.testsem7v1

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.acrcloud.rec.ACRCloudClient
import com.acrcloud.rec.ACRCloudConfig
import com.acrcloud.rec.ACRCloudResult
import com.acrcloud.rec.IACRCloudListener
import com.acrcloud.rec.utils.ACRCloudLogger
import com.example.testsem7v1.ACRCloud.acrResponse
import com.example.testsem7v1.ACRCloud.humming.acrHumResponse
import com.example.testsem7v1.activity.ACRHumResponse
import com.example.testsem7v1.activity.ACRResponse
import com.example.testsem7v1.activity.accessToken
import com.example.testsem7v1.activity.dataStore
import com.example.testsem7v1.activity.gameData
import com.example.testsem7v1.activity.isAudio
import com.example.testsem7v1.activity.isPageLoaded
import com.example.testsem7v1.activity.userID
import com.example.testsem7v1.adapter.homeAlbumRVAdapter
import com.example.testsem7v1.adapter.homeMusicRVAdapter
import com.example.testsem7v1.interfaces.optionItemClickListener
import com.example.testsem7v1.interfaces.spotifyItemClickListener
import com.example.testsem7v1.popup.clickOptionPopUp
import com.example.testsem7v1.popup.identifiedHumPopUp
import com.example.testsem7v1.popup.identifiedPopUp
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.spotify.recommendation.recommendationResponse
import com.example.testsem7v1.retrofit.spotify.sAccessToken
import com.example.testsem7v1.retrofit.systemDatabase.gameCreate
import com.example.testsem7v1.retrofit.systemDatabase.gameUpdate
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
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
class HomeFragment : Fragment(), IACRCloudListener, spotifyItemClickListener,
    optionItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //ACRCloud
    private var mClient: ACRCloudClient? = null


    //Button
    lateinit var recordButton: ImageButton

    // put here so it doesnt run again.
//    lateinit var musicRecyclerAdapter: homeMusicRVAdapter
    var musicRecyclerAdapter = homeMusicRVAdapter()
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
        Log.e("START", "oncreate")
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


        //Button Init
        recordButton = view.findViewById(R.id.recordButton)

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

        //Initialise callBack from adapter
        musicRecyclerAdapter.listener = this
        albumRecyclerAdapter.listener = this
        Log.e("HomeFragment", "Is page Loaded? : $isPageLoaded")

        // Token & RecyclerView populate ====================================
        lifecycleScope.launchWhenCreated {
            if (musicRecyclerAdapter.recommendationTrack.isNotEmpty()) {
                musicProgressbar.isVisible = false
            } else {
                musicProgressbar.isVisible = true
                albumProgressbar.isVisible = true
            }

            var isValidToken = isTokenValid()
            Log.e("HomeFragment", "Is Token Valid?: $isValidToken")
            //ms/1000 = s , s/60 = minutes


            var requestTime = (getRequestTime() / 1000) / 60 //in minutes

            Log.e("HomeFragment", "Request exceeded time: $requestTime minute")
            // only refresh if more than 10 minutes or if !isPageLoaded aka Application Restart
            if (isValidToken && requestTime > 10) {
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
                    isPageLoaded = saveRequestTime()
                } else {
                    Log.e("ActivityMain", "Response not successful")
                }

            } else if (!isValidToken && requestTime > 10) {
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
                        var recommendationData: recommendationResponse =
                            recommendationResponse.body()!!

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
                        isPageLoaded = saveRequestTime()
                    } else {
                        Log.e("ActivityMain", "Response not successful")
                    }


                } else {
                    Log.e("ActivityMain", "Token retrieval not successful")
                }
            } else if (!isPageLoaded) {
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
                        var recommendationData: recommendationResponse =
                            recommendationResponse.body()!!

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
                        isPageLoaded = saveRequestTime()
                    } else {
                        Log.e("ActivityMain", "Response not successful")
                    }


                } else {
                    Log.e("ActivityMain", "Token retrieval not successful")
                }
            }


        }
        // =============== end ==============

        initACRCloud()
        recordButton.setOnClickListener {
            checkPermission()
        }


        // Check laer
//        var statusAnimation = false
//        Log.e("STATUSANIMATION", statusAnimation.toString())
//        if (statusAnimation){
//            stopPulse()
//            statusAnimation = false
//        }
//        else{
//            startPulse()
//            statusAnimation = true
//        }

    }

    // ACRCloud
    private fun initACRCloud() {
        val config = ACRCloudConfig()

        config.acrcloudListener = this
        config.context = this.activity

        config.host = "identify-ap-southeast-1.acrcloud.com"
        //Old one only for audio
//        config.accessKey = "f679a64651655bcd2b692aedd14526fc"
//        config.accessSecret = "mlFB5Vs7Ne2xOt3ASz9KC3IHgMmayGO7o6cONbDQ"
        //new one for humming
        config.accessKey = "11773ecc1a9b6854a0412a908d9f8f52"
        config.accessSecret = "x10QSYfQzbOtGA91GWj91IO9mLOdUs8yVnIwBRhp"

        config.recorderConfig.rate = 8000
        config.recorderConfig.channels = 1

        mClient = ACRCloudClient()
        //DEBUG
        ACRCloudLogger.setLog(true)

        mClient!!.initWithConfig(config)

    }


    override fun onResult(acrResult: ACRCloudResult?) {
        acrResult?.let {
            Log.d("ACR", "${it.result}")
            stopPulse()


            val gson = Gson()
            var jsonString: String = it.result

            if (it.result.contains("humming")) {
                val responseHum = gson.fromJson(jsonString, acrHumResponse::class.java)
                Log.e("HUMM", "This is a hum")
                ACRHumResponse = responseHum
                isAudio = false
                val popup = identifiedHumPopUp()
                popup.show(requireActivity().supportFragmentManager, "humpopup")
            } else {
                val response = gson.fromJson(jsonString, acrResponse::class.java)
                Log.e("CHECKRESULT", response.metadata.music[0].title)
                Log.e("Audio", "This is an Audio")
                ACRResponse = response
                isAudio = true
                val popup = identifiedPopUp()
                popup.show(requireActivity().supportFragmentManager, "popup")
            }



            //Scoring
            var gameExist = "failed"
            if (userID.accountID > 0) {
                //play music
                val mediaPlayer = MediaPlayer.create(context, R.raw.ding)
                mediaPlayer.start()
                //getGameData by user ID
                lifecycleScope.launch {
                    gameExist = getGame()
//                    Log.e("GAMETAG", "UserID: ${gameData.gameID.toString()}")

                    //getScore
                    Log.e("GAMETAG", "Check Test")

                    if (gameExist == "failed") {
                        //create new game
                        Log.e("TESTHOMEFRAGMENT", "no score.")


                        var isGameCreated: String = createGame()
                        Log.e("GAME CREATED", "LOLZ")

                        if (isGameCreated == "Success") {
                            lifecycleScope.launch {
                                getGame()
                                Log.e("GAMETAG", "UserID: ${gameData.gameID.toString()}")

                                ++gameData.score
                                updateGame(gameData.score)
                                Log.e("GAMETAG", "Score: ${gameData.score.toString()}")
                            }
                        }
                    }
                    ++gameData.score
                    updateGame(gameData.score)
                    Log.e("GAMETAG", "Score: ${gameData.score.toString()}")
                }
            }
        }

    }

    override fun onVolumeChanged(volume: Double) {
        Log.d("ACR", "volume changed $volume")
    }

    private fun startRecognition() {

        mClient?.let {
            if (it.startRecognize()) {
                startPulse()
            } else {
                stopPulse()
                Log.e("ACRCloud", "Initialization Error")
            }
        } ?: run {
            Log.e("ACRCloud", "Client not ready")
        }
    }


    // Permission Check
    private fun checkPermission() {
        if (ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                android.Manifest.permission.RECORD_AUDIO
            ) != 0
        ) {
            Log.e("checkPermission", "NO PERMISSION!")
//            recordButton.setOnClickListener{
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(android.Manifest.permission.RECORD_AUDIO),
                100
            )
//            }
        } else {
            hasPermission()
        }
    }

    private fun hasPermission() {
        recordButton.setOnClickListener {
            startRecognition()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            checkPermission()
        }
    }


    //Microphone Animation Functions
    private fun startPulse() {
        Log.e("STATUSANIMATION", "START PULSE")
        runnable.run()
    }

    private fun stopPulse() {
        Log.e("STATUSANIMATION", "STOP PULSE")
        handlerAnimation.removeCallbacks(runnable)
    }

    private var handlerAnimation = Handler()
    private var runnable = object : Runnable {
        override fun run() {
            var animation: ImageView = requireView().findViewById(R.id.recordButtonAnimation)

            animation.animate().scaleX(4f).scaleY(4f).alpha(0f).setDuration(1000)
                .withEndAction {
                    animation.scaleX = 1f
                    animation.scaleY = 1f
                    animation.alpha = 1f
                }
            handlerAnimation.postDelayed(this, 1500)
        }
    }

    private suspend fun isTokenValid(): Boolean {

        val current_time = System.currentTimeMillis()
        // datastore.data retrieves the value in dataStore

        val created_token_time = requireContext().dataStore.data.map {
            //transforms it into an int using .map [] ?: 0,
                accessToken ->
            accessToken[longPreferencesKey("created_timestamp")] ?: null
        }.first()

        if (created_token_time == null) {
            return false
        }


        Log.e("HomeFragmentIsTokenValid()", "created_token_time: ${created_token_time!!}")
        val time_exceeded = current_time - created_token_time!!

        Log.e("HomeFragment", "Token Time Exceeded: ${(time_exceeded / 1000) / 60} minutes")

        // millisecond/1000 = seconds
        if (time_exceeded / 1000 > 3600) {
            return false
        }

        return true
    }

    private suspend fun getStoredToken() {
        var access_token = requireContext().dataStore.data.map { accessToken ->
            accessToken[stringPreferencesKey("access_token")] ?: null
        }.first()!!

        var expires_in = requireContext().dataStore.data.map { accessToken ->
            accessToken[intPreferencesKey("expires_in")] ?: null
        }.first()!!

        var token_type = requireContext().dataStore.data.map { accessToken ->
            accessToken[stringPreferencesKey("token_type")] ?: null
        }.first()!!

        val stored_accessToken = sAccessToken(
            access_token = access_token,
            expires_in = expires_in,
            token_type = token_type
        )
        accessToken = stored_accessToken
    }

    private suspend fun saveToken(token: sAccessToken) {
        requireContext().dataStore.edit { accessToken ->
            accessToken[stringPreferencesKey("access_token")] = token.access_token
            accessToken[intPreferencesKey("expires_in")] = token.expires_in
            accessToken[stringPreferencesKey("token_type")] = token.token_type
            accessToken[longPreferencesKey("created_timestamp")] = System.currentTimeMillis()
            Log.e("HomeFragmentAccessToken", "created_timestamp: ${System.currentTimeMillis()}")

        }
    }

    private suspend fun saveRequestTime(): Boolean {
        requireContext().dataStore.edit { accessToken ->
            accessToken[longPreferencesKey("created_request_timestamp")] =
                System.currentTimeMillis()
        }
        Log.e("HomeFragment", "DEBUG HERE")
        return true
    }

    private suspend fun getRequestTime(): Long {

        var timestamp = requireContext().dataStore.data.map { accessToken ->
            accessToken[longPreferencesKey("created_request_timestamp")] ?: null
        }.first()

        if (timestamp == null) {
            return 660000
        }

        var exceed_time = System.currentTimeMillis() - timestamp

        return exceed_time
    }

    // Popup Window


    override fun onDestroy() {
        super.onDestroy()
        mClient?.let {
            it.release()
            mClient = null
        }
    }

    private suspend fun getGame(): String {

        var id = userID.accountID
        Log.e("HOMEFRAGMENT-GETGOOD", id.toString())
        val response = try {
            retrofitInstance.api.getGame(id)
        } catch (e: IOException) {
            Log.e("HOMEFRAGMENT-GETGOOD", "IOException, you might not have internet connection")
            return "failed"

        } catch (e: HttpException) {
            Log.e("HOMEFRAGMENT-GETGOOD", "HttpException, unexpected response")
            return "failed"
        }
        if (response.isSuccessful && response.body() != null) {
            gameData = response.body()!!
        }

        return "Success"
    }

    private suspend fun createGame(): String {

        var id = userID.accountID
        var gameCreateData: gameCreate = gameCreate(accountID = id)
        Log.e("HOMEFRAGMENT-create", id.toString())
        val response = try {
            retrofitInstance.api.addGame(gameCreateData)
        } catch (e: IOException) {
            Log.e("HOMEFRAGMENT-create", "IOException, you might not have internet connection")
            return ""

        } catch (e: HttpException) {
            Log.e("HOMEFRAGMENT-create", "HttpException, unexpected response")
            return ""
        }
        if (response.isSuccessful && response.body() != null) {
            Log.e("HOMEFRAGMENT-create", response.body().toString())

        }

        return "Success"
    }

    private suspend fun updateGame(score: Int): String {

        Log.e("SCORESCORE", "${score}")
        var id = userID.accountID
        var gameUpdateData: gameUpdate = gameUpdate(score)
        Log.e("HOMEFRAGMENT-update", id.toString())
        val response = try {
            retrofitInstance.api.updateGame(id, gameUpdateData)
        } catch (e: IOException) {
            Log.e("HOMEFRAGMENT-update", "IOException, you might not have internet connection")
            return ""

        } catch (e: HttpException) {
            Log.e("HOMEFRAGMENT-update", "HttpException, unexpected response")
            return ""
        }
        if (response.isSuccessful && response.body() != null) {
            Log.e("HOMEFRAGMENT-update", response.body().toString())
        }

        return "Success"
    }


    override fun passTrackRef(id: String) {
        Log.e("passTrackRef", "ID pressed: $id")
        val popup = clickOptionPopUp()
        val bundle = Bundle()
        bundle.putString("SpotifyRef", id)
        bundle.putString("Type", "Track")
        popup.arguments = bundle
        popup.listener = this
        popup.show(requireActivity().supportFragmentManager, "option")

    }

    override fun passAlbumRef(id: String) {
        Log.e("passAlbumRef", "ID pressed: $id")
        val popup = clickOptionPopUp()
        val bundle = Bundle()
        bundle.putString("SpotifyRef", id)
        bundle.putString("Type", "Album")
        popup.arguments = bundle
        popup.listener = this
        popup.show(requireActivity().supportFragmentManager, "option")
    }

    override fun viewArtistData(id: String) {
        val aboutArtistFragment = aboutArtistFragment()
        val bundle = Bundle()
        bundle.putString("artistRef", id)
        aboutArtistFragment.arguments = bundle

        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, aboutArtistFragment).addToBackStack("artist")
            .commit()
    }


    override fun viewAlbumData(id: String) {
        super.viewAlbumData(id)

        val viewAlbum = viewAlbum()
        val bundle = Bundle()
        bundle.putString("albumRef", id)
        viewAlbum.arguments = bundle

        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, viewAlbum).addToBackStack("album").commit()
    }
}


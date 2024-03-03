package com.example.testsem7v1.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.core.view.forEach
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.testsem7v1.ACRCloud.acrResponse
import com.example.testsem7v1.ACRCloud.humming.acrHumResponse
import com.example.testsem7v1.GameFragment
import com.example.testsem7v1.HomeFragment
import com.example.testsem7v1.PlaylistFragment
import com.example.testsem7v1.ProfileFragment
import com.example.testsem7v1.R
import com.example.testsem7v1.SearchFragment
import com.example.testsem7v1.databinding.ActivityMainBinding
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.spotify.sAccessToken
import com.example.testsem7v1.retrofit.systemDatabase.gameCreate
import com.example.testsem7v1.retrofit.systemDatabase.gameData
import com.example.testsem7v1.retrofit.systemDatabase.userID
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

var loginSession = false
var admin = false
var isAudio = false
var mainBackstack = 1
lateinit var userID: userID
lateinit var accessToken: sAccessToken
lateinit var ACRResponse: acrResponse
lateinit var ACRHumResponse: acrHumResponse
lateinit var gameData: gameData

lateinit var backPressedCallback: OnBackPressedCallback

var prev_menuID = R.id.Home


// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "accessToken")
var isPageLoaded = false // Home fragment

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {


        OnBackPressedDispatcher {

            var count = supportFragmentManager.backStackEntryCount

            Log.e("BACKPRESSMAIN", "count: $count, mainbackstack: $mainBackstack")

            if (count > mainBackstack) {
                super.onBackPressedDispatcher
            }
        }

        // Backbutton Handler
        backPressedCallback = object :
            OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.e("BACKPRESS", "TEST")

                var count = supportFragmentManager.backStackEntryCount

                if (count > 0) {
                    supportFragmentManager.popBackStack()
                }
            }

        }

        Log.e("MAINACTIVITY", "ACTIVITY ONCREATE")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, LoginActivity::class.java)
        binding = ActivityMainBinding.inflate(layoutInflater)
//        val loginBinding: ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        val view = binding.root

        setContentView(binding.root)
        Log.i("TAG", "hello")

        // =================================== SPOTIFY TEST ===================================

        Log.e("SPOTIFYTESTMAIN", "Test getting credentials")
        //Since this is a lifecycle we need to wait for this to make accessToken available.
        // To do that we nest teh code on top of each ther.


        // ====================================END==================================================

//         Menu Item Refresh //

//         =====

        //Whenever app initialized, starts at home.
//        replaceFragment(HomeFragment())
        var item_selected = inithomeFrag(HomeFragment())
        prev_menuID = R.id.Home

//        binding.bottomNav.menu.findItem(R.id.Home).setChecked(true)

        binding.bottomNav.menu.findItem(R.id.Badge).isVisible = loginSession
        binding.bottomNav.menu.findItem(R.id.Playlist).isVisible = loginSession

//        var mainBackstack = 1

        //clicked
        var searchClicked = false
        var badgeClicked = false
        var playlistClicked = false
        var profileClicked = false
        binding.bottomNav.itemIconTintList = null
        binding.bottomNav.setOnItemSelectedListener {
            // Fragment transaction error fix
            val count = supportFragmentManager.backStackEntryCount
            if (count > mainBackstack) {
                Toast.makeText(
                    baseContext,
                    "Please go back to the main page first!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnItemSelectedListener false
            }

            Log.e(
                "SELECTED",
                "item_selected: $item_selected, backstack: $count, mainbackstack: $mainBackstack"
            )
            if (count == mainBackstack) {

                when (it.itemId) {
                    R.id.Home -> {
                        if (it.itemId != prev_menuID) {
                            item_selected = replaceFragment(HomeFragment(), item_selected)
                            prev_menuID = R.id.Home
                        }
                    }

                    R.id.Search -> {
                        if (it.itemId != prev_menuID) {
                            item_selected = replaceFragment(SearchFragment(), item_selected)
                            prev_menuID = R.id.Search
                            if (!searchClicked) {
                                mainBackstack++
                                searchClicked = true
                            }
                        }
                    }

                    R.id.Badge -> {
                        if (it.itemId != prev_menuID) {
                            item_selected = replaceFragment(GameFragment(), item_selected)
                            prev_menuID = R.id.Badge

                            if (!badgeClicked) {
                                badgeClicked = true
                                mainBackstack++
                            }
                        }
                    }

                    R.id.Playlist -> {
                        if (it.itemId != prev_menuID) {
                            item_selected = replaceFragment(PlaylistFragment(), item_selected)
                            prev_menuID = R.id.Playlist

                            if (!playlistClicked) {
                                playlistClicked = true
                                mainBackstack++
                            }

                        }
                    }

                    //Login-Register-Profile
                    //1. If there is no session, go directly to login page.
                    //2. Else go to profile


//                R.id.Profile -> replaceFragment(ProfileFragment())
//                R.id.Profile -> setContentView(loginBinding.root)

                    R.id.Profile -> {
                        if (!loginSession) {
                            Log.e("PROFILE TAG", "INSIDE PROFILE")
                            startActivity(intent)
                            return@setOnItemSelectedListener false
                        } else {
                            if (it.itemId != prev_menuID) {
                                item_selected = replaceFragment(ProfileFragment(), item_selected)
                                prev_menuID = R.id.Profile

                                if (!profileClicked) {
                                    profileClicked = true
                                    mainBackstack++
                                }
                            }
                        }
                    }


                    else -> {

                    }

                }
            }

            true
        }
    }

    override fun onResume() {
        super.onResume()
        Log.e("ONRESUME", loginSession.toString())
        binding.bottomNav.menu.findItem(R.id.Badge).isVisible = loginSession
        binding.bottomNav.menu.findItem(R.id.Playlist).isVisible = loginSession
        Log.e("ONRESUME", "RESUMED")
//        binding.bottomNav.selectedItemId = R.id.Home
//        prev_menuID=R.id.Home

        var gameExist = "failed"
        if (loginSession) {
            //getGameData by user ID
            lifecycleScope.launch {
//                coroutineScope {
                    lifecycleScope.launch {
                        Log.e("TESTMAINACTIVITY", "coroutine1")
                        gameExist = getGame()
//                    Log.e("GAMETAG", "UserID: ${gameData.gameID.toString()}")
                        Log.e("TestGame", "gameExist: $gameExist")
                        if (gameExist == "failed") {
                            //create new game
                            Log.e("TestGame", "Game does not exist, creating game")

                            var isGameCreated: String = createGame()

                            if (isGameCreated == "Success") {
                                lifecycleScope.launch {
                                    getGame()
                                    Log.e("GAMETAG", "UserID: ${gameData.gameID.toString()}")
                                    loadBadge()
                                }
                            }
                        } else{
                            Log.e("TestGame", "Game exists")
                            loadBadge()
                        }
                    }

//                }
            }
        }
    }

    private fun loadBadge() {
        if (gameData.score in 100..249) {
            binding.bottomNav.menu.findItem(R.id.Badge)
                .setIcon(R.drawable.basic_removebg_preview)
        } else if (gameData.score in 250..999) {
            binding.bottomNav.menu.findItem(R.id.Badge)
                .setIcon(R.drawable.advanced_removebg_preview)
        } else if (gameData.score in 1000..2499) {
            binding.bottomNav.menu.findItem(R.id.Badge)
                .setIcon(R.drawable.pro_removebg_preview_1)
        } else if (gameData.score >= 2500) {
            binding.bottomNav.menu.findItem(R.id.Badge).setIcon(R.drawable.premium_removebg_preview)
        }
    }

    //Replace fragment.
    private fun replaceFragment(fragment: Fragment, selected: String): String {
        val fragmentManager = supportFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()

        val passedfrag =
            fragmentManager.findFragmentByTag(fragment.javaClass.simpleName)
        val selectedFrag = fragmentManager.findFragmentByTag(selected)
        if (passedfrag != null) {
            Log.e("isadded", "this fragment is added bitch")
            //how to detach the same previous fragment?
            fragmentTransaction.attach(passedfrag)
            fragmentTransaction.detach(selectedFrag!!)//previousFragment,first time ok, second time no because
            //first time, goes to else which adds search to backstackentry
            //second time, when we go home. Home is passed, backstack entry is search
            // therefore attach and detach works
            // goes to search, search is passed, but backstack entry is still search so home is not detach.
            // how to get home?
            // how to get current fragment from pressing navigation?
            // pressed: search, from home.
            //
            fragmentTransaction.commit()
        } else {
            fragmentTransaction.detach(getPreviousFragment()!!) //inithome works.
            fragmentTransaction.add(
                R.id.frameLayout,
                fragment,
                fragment.javaClass.simpleName
            )
            fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)

            fragmentTransaction.commit()
        }

        return fragment.javaClass.simpleName
    }

    private fun inithomeFrag(fragment: Fragment): String {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(
            R.id.frameLayout,
            fragment,
            fragment.javaClass.simpleName
        )
        fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
        fragmentTransaction.commit()

        return javaClass.simpleName

    }


    private fun getPreviousFragment(): Fragment? {
        if (supportFragmentManager.backStackEntryCount == 0) {
            return null
        }

        var tag =
            supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name

        Log.e(
            "getcurrentfragment", "${
                supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
            }"
        )
        return supportFragmentManager.findFragmentByTag(tag)
    }

    private fun checkCurrentFragment() {
        supportFragmentManager.backStackEntryCount
    }

    private suspend fun getGame(): String {

        var id = userID.accountID
        Log.e("HOMEFRAGMENT-GETGOOD", id.toString())
        val response = try {
            retrofitInstance.api.getGame(id)
        } catch (e: IOException) {
            Log.e(
                "HOMEFRAGMENT-GETGOOD",
                "IOException, you might not have internet connection"
            )
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
            Log.e(
                "HOMEFRAGMENT-create",
                "IOException, you might not have internet connection"
            )
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


//    var count = supportFragmentManager.backStackEntryCount
//
//        Log.e("BACKPRESSMAIN", "count: $count, mainbackstack: $mainBackstack")
//
//        if(count > mainBackstack){
//            super.onBackPressed()
}




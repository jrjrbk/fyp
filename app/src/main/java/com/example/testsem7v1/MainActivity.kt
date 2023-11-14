package com.example.testsem7v1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.testsem7v1.databinding.ActivityMainBinding
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.spotify.sAccessToken
import com.example.testsem7v1.retrofit.spotify.test
import com.example.testsem7v1.retrofit.userID
import com.squareup.picasso.Picasso
import retrofit2.HttpException
import java.io.IOException

var loginSession = false
lateinit var userID: userID
lateinit var accessToken: sAccessToken
lateinit var artistData: test
var accessTokenAvailable = false

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, LoginActivity::class.java)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
//        val loginBinding: ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        val view = binding.root
        setContentView(binding.root)
            Log.i("TAG","hello")

        // =================================== SPOTIFY TEST ===================================

        Log.e("SPOTIFYTESTMAIN", "Test getting credentials")
        //Since this is a lifecycle we need to wait for this to make accessToken available.
        // To do that we nest teh code on top of each ther.
        lifecycleScope.launchWhenCreated {
            val response = try{
                retrofitInstance.api.getToken("client_credentials","20d283513f3b4ea79bb4d0d35b975762","02fb3bee5b6a4f6598fcc0c28f9a0ce2")
            } catch (e: IOException){
                Log.e("MainActivity", "IOException, you might not have internet connection")
                Log.e("Error",throw(e))
                return@launchWhenCreated // so that the thread can resume.
            } catch (e: HttpException){
                Log.e("MainActivity", "HttpException, unexpected response")
                return@launchWhenCreated // so that the thread can resume.
            }

            if (response.isSuccessful && response.body() != null) {
                Log.e("ActivityMain", "Response successful CODE:${response.code()}")
                accessToken = response.body()!!
                Log.e("ActivityMain-ACCESSTOKENTEST", accessToken.access_token)

                val artistResponse = try {
                    val authTest = "${accessToken.token_type} ${accessToken.access_token}"
                    Log.e("TESTAUTHTEST", authTest)
                    retrofitInstance.api.getArtist(authTest)
                } catch (e: IOException) {
                    Log.e("MainActivity", "IOException, you might not have internet connection")
                    Log.e("Error", throw (e))
                    return@launchWhenCreated // so that the thread can resume.
                } catch (e: HttpException) {
                    Log.e("MainActivity", "HttpException, unexpected response")
                    return@launchWhenCreated // so that the thread can resume.
                }
                if (artistResponse.isSuccessful && artistResponse.body() != null) {
                    Log.e("ActivityMain", "Response successful CODE:${artistResponse.code()}")
                    artistData = artistResponse.body()!!
                    Log.e("ACTIVITYMAIN", artistData.name)
                    val textviewtest: TextView = findViewById(R.id.homeMainTV)
                    textviewtest.text = artistData.name
                    val imageviewtest: ImageView = findViewById(R.id.homeImageView2)
                    val imageviewtest2: ImageView = findViewById(R.id.homeImageView3)
                    Picasso.get().load(artistData.images[0].url).into(imageviewtest)
                    Picasso.get().load(artistData.images[1].url).into(imageviewtest2)

                } else {
                    Log.e("ActivityMain", "Response not successful")
                }
            }else{
                Log.e("ActivityMain", "Token retrieval not successful")
            }
        }

        // ====================================END==================================================


        //Whenever app initialized, starts at home.
        replaceFragment(HomeFragment())

        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.Home -> replaceFragment(HomeFragment())
                R.id.Search -> replaceFragment(SearchFragment())
                R.id.Badge -> replaceFragment(SearchFragment())
                R.id.Playlist -> replaceFragment(PlaylistFragment())
                
                //Login-Register-Profile
                //1. If there is no session, go directly to login page.
                //2. Else go to profile
                
                
//                R.id.Profile -> replaceFragment(ProfileFragment())
//                R.id.Profile -> setContentView(loginBinding.root)

                R.id.Profile -> {
                    if(!loginSession){
                        startActivity(intent)
                    }
                    else{
                        replaceFragment(ProfileFragment())
                    }
                }



                else ->{

                }

            }

            true
        }
    }


    //Replace fragment.
    private fun replaceFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction =fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, fragment)
        fragmentTransaction.commit()
    }



}



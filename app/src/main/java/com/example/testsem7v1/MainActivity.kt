package com.example.testsem7v1

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import com.example.testsem7v1.databinding.ActivityMainBinding
import com.example.testsem7v1.retrofit.spotify.sAccessToken
import com.example.testsem7v1.retrofit.userID

var loginSession = false
lateinit var userID: userID
lateinit var accessToken: sAccessToken
var accessTokenAvailable = false

// At the top level of your kotlin file:
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "accessToken")
var isPageLoaded = false // Home fragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, LoginActivity::class.java)
        val binding: ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        binding.bottomNav.selectedItemId = R.id.Home
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


        //Whenever app initialized, starts at home.
//        replaceFragment(HomeFragment())
        var item_selected = inithomeFrag(HomeFragment())


        binding.bottomNav.setOnItemSelectedListener {
            Log.e("SELECTED", "$item_selected")
            when (it.itemId) {

                R.id.Home -> item_selected=replaceFragment(HomeFragment(),item_selected)

                R.id.Search -> {
                    item_selected=replaceFragment(SearchFragment(),item_selected)
                }

                R.id.Badge -> item_selected=replaceFragment(SearchFragment(),item_selected)
                R.id.Playlist -> item_selected=replaceFragment(PlaylistFragment(),item_selected)

                //Login-Register-Profile
                //1. If there is no session, go directly to login page.
                //2. Else go to profile


//                R.id.Profile -> replaceFragment(ProfileFragment())
//                R.id.Profile -> setContentView(loginBinding.root)

                R.id.Profile -> {
                    if (!loginSession) {
                        startActivity(intent)
                    } else {
                        replaceFragment(ProfileFragment(),item_selected)
                    }
                }


                else -> {

                }

            }

            true
        }
    }


    //Replace fragment.
    private fun replaceFragment(fragment: Fragment, selected: String): String {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val passedfrag = fragmentManager.findFragmentByTag(fragment.javaClass.simpleName)
        val selectedFrag = fragmentManager.findFragmentByTag(selected)
        if(passedfrag != null){
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
        }

        else{

            fragmentTransaction.detach(getPreviousFragment()!!) //inithome works.
            fragmentTransaction.add(R.id.frameLayout, fragment, fragment.javaClass.simpleName)
            fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)

        fragmentTransaction.commit()

        }

        return fragment.javaClass.simpleName
    }

    private fun inithomeFrag(fragment: Fragment):String  {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frameLayout, fragment, fragment.javaClass.simpleName)
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

        Log.e("getcurrentfragment", "${supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name
        }")
        return supportFragmentManager.findFragmentByTag(tag)
    }

    private fun checkCurrentFragment(){
        supportFragmentManager.backStackEntryCount
    }

}



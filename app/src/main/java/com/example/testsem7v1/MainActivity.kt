package com.example.testsem7v1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.databinding.ActivityLoginBinding
import com.example.testsem7v1.databinding.ActivityMainBinding

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

        //Simulate session
        val loginSession = true


        //Whenever app initialized, starts at home.
        replaceFragment(HomeFragment())

        binding.bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.Home -> replaceFragment(HomeFragment())
                R.id.Search -> replaceFragment(ProfileFragment())
                R.id.Badge -> replaceFragment(SettingFragment())
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



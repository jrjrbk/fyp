package com.example.testsem7v1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.testsem7v1.databinding.ActivityLoginBinding
import com.example.testsem7v1.databinding.ActivityRegisterBinding
import com.example.testsem7v1.retrofit.LoginRequest
import com.example.testsem7v1.retrofit.retrofitInstance
import retrofit2.HttpException
import java.io.IOException


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.i("TAG","hello2")


        val loginBinding2: ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(loginBinding2.root)

        loginBinding2.loginButton.setOnClickListener{
            val email = loginBinding2.editTextTextEmail.text.toString().trim()
            val password = loginBinding2.editTextPassword.text.toString().trim()

            var loginRequest = LoginRequest(email = email, password = password)

            val intent = Intent(this, MainActivity::class.java)
            lifecycleScope.launchWhenCreated {

                val response = try{
                    retrofitInstance.api.loginUser(loginRequest)
                } catch (e: IOException){
                    Log.e("MainActivity", "IOException, you might not have internet connection")
                    Log.e("Error",throw(e))
                    return@launchWhenCreated // so that the thread can resume.

                } catch (e: HttpException){ // 3 digit code, if does not start with a 2. ERROR
                    Log.e("MainActivity", "HttpException, unexpected response")
                    return@launchWhenCreated // so that the thread can resume.
                }
                if(response.isSuccessful && response.body() != null){
                    // sets the students list to response.body() which is the json file.
                    Log.e("LoginActivity","SUCCESS!")
                    Log.e("LoginActivity",response.code().toString())
                    userID = response.body()!!
                    Log.e("LoginActivity",userID.accountID.toString())
                    loginSession=true
                    finish()
                    startActivity(intent)
                    // !! means can be null
                } else{
                    Log.e("LoginActivity", "Response not successful")
                }
            }
        }

        loginBinding2.SignUpButtonLogin.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

}
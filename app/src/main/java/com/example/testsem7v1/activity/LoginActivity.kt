package com.example.testsem7v1.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.testsem7v1.R
import com.example.testsem7v1.databinding.ActivityLoginBinding
import com.example.testsem7v1.retrofit.systemDatabase.LoginRequest
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
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            lifecycleScope.launchWhenCreated {

                // loginUser
                val response = try{
                    retrofitInstance.api.loginUser(loginRequest)
                } catch (e: IOException){
                    Log.e("MainActivity", "IOException, you might not have internet connection")
                    Log.e("Error",throw(e))
                    Toast.makeText(this@LoginActivity,"Login Unsuccessful, problem connecting!",Toast.LENGTH_LONG).show()
                    return@launchWhenCreated // so that the thread can resume.

                } catch (e: HttpException){ // 3 digit code, if does not start with a 2. ERROR
                    Log.e("MainActivity", "HttpException, unexpected response")
                    return@launchWhenCreated // so that the thread can resume.
                }
                if(response.isSuccessful && response.body() != null){
                    // sets the students list to response.body() which is the json file.
                    Toast.makeText(this@LoginActivity,"Logging in",Toast.LENGTH_LONG).show()
                    Log.e("LoginActivity","SUCCESS!")
                    Log.e("LoginActivity",response.code().toString())
                    userID = response.body()!!

                    Log.e("LoginActivity", userID.accountID.toString())
                    loginSession =true

                    if(email.contains("admin")){
                        admin = true
                        Log.e("IsadminTrue", admin.toString())
                    }
                    finish()
                    startActivityIfNeeded(intent,0)
                    // !! means can be null
                } else{
                    Toast.makeText(this@LoginActivity,"Login Unsuccessful!",Toast.LENGTH_LONG).show()
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
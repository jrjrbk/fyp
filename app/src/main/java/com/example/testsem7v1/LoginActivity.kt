package com.example.testsem7v1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.testsem7v1.databinding.ActivityLoginBinding
import com.example.testsem7v1.databinding.ActivityRegisterBinding


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.i("TAG","hello2")

        val intent = Intent(this, RegisterActivity::class.java)

        val loginBinding2: ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(loginBinding2.root)

        loginBinding2.SignUpButtonLogin.setOnClickListener {
            Log.i("TAG","toli")
            startActivity(intent)
        }
    }

}
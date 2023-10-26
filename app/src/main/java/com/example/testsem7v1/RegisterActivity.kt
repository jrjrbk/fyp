package com.example.testsem7v1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.testsem7v1.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, LoginActivity::class.java)
        val registerBinding: ActivityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(registerBinding.root)

        registerBinding.loginButtonReg.setOnClickListener {
            startActivity(intent)
        }
    }
}
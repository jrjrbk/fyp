package com.example.testsem7v1

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import com.example.testsem7v1.databinding.ActivityRegisterBinding
import com.example.testsem7v1.retrofit.Users
import com.example.testsem7v1.retrofit.retrofitInstance
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.http.Body
import java.io.IOException
import java.time.LocalDate

class RegisterActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val registerBinding: ActivityRegisterBinding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(registerBinding.root)

        registerBinding.regButton.setOnClickListener{
            val email = registerBinding.editTextEmailReg.text.toString().trim()
            val password = registerBinding.editTextPasswordReg.text.toString().trim()
            val confirmPassword = registerBinding.editTextConfirmPassword.text.toString().trim()

            if(email.isEmpty()){
                registerBinding.editTextEmailReg.error = "Email Required!"
                registerBinding.editTextEmailReg.requestFocus()
                return@setOnClickListener
            }



            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                registerBinding.editTextEmailReg.error = "Email is not valid!"
                registerBinding.editTextEmailReg.requestFocus()
                return@setOnClickListener
            }

            if(password.isEmpty()){
                registerBinding.editTextPasswordReg.error = "Password Required!"
                registerBinding.editTextPasswordReg.requestFocus()
                return@setOnClickListener
            }

            if(password.length < 8){
                registerBinding.editTextPasswordReg.error = "Password is less than 8 characters!"
                registerBinding.editTextPasswordReg.requestFocus()
                return@setOnClickListener
            }

            if(confirmPassword != password){
                registerBinding.editTextConfirmPassword.error = "Password does not match!"
                registerBinding.editTextConfirmPassword.requestFocus()
                return@setOnClickListener
            }

            val userInfo = Users(   email = email,
                                    password = password,
                                    username = email,
                                    dateCreated = LocalDate.now().toString())

            addUser(userInfo){
                if(it?.email != null){
                    //it the User data
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Log.d("RegisterActivity", "Error registering user")
                }
            }

        }

        registerBinding.loginButtonReg.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addUser(userData: Users, onResult: (Users?) -> Unit) {
        retrofitInstance.api.createUser(userData).enqueue(
            object: Callback<Users>{
                override fun onResponse(call: Call<Users>, response: Response<Users>) {
                    if(response.code() == 201){
                        Toast.makeText(applicationContext, "Register successful!",Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(applicationContext, "Register unsuccessful!",Toast.LENGTH_LONG).show()
                    }
                    val addedUsers = response.body()
                    onResult(addedUsers)
                }

                override fun onFailure(call: Call<Users>, t: Throwable) {
                    //Fuck it goes here, but it does make the system.
                    Toast.makeText(applicationContext,t.message,Toast.LENGTH_LONG).show()
                    Log.e("Register","Error" ,t)
                    onResult(null)
                }

            }
        )
    }


}
package com.example.testsem7v1

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.testsem7v1.activity.userID
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.systemDatabase.aboutResponse
import com.example.testsem7v1.retrofit.systemDatabase.imagePathResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


class editAbout : Fragment() {

    private lateinit var aboutData: List<aboutResponse>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val editversion: EditText = requireView().findViewById(R.id.editaboutVersionInfo)
        val editEmail: EditText = requireView().findViewById(R.id.editaboutContactEmailInfo)
        val editNumber: EditText = requireView().findViewById(R.id.editaboutContactNumberInfo)
        val editInfo: EditText = requireView().findViewById(R.id.editaboutUsInfo)
        val editAboutConfirm: Button = requireView().findViewById(R.id.editAboutConfirm)

        lifecycleScope.launch {
            getAbout()
            editversion.setText(aboutData[0].aboutVersion)
            editEmail.setText(aboutData[0].aboutEmail)
            editNumber.setText(aboutData[0].aboutNumber)
            editInfo.setText(aboutData[0].aboutInfo)

            editAboutConfirm.setOnClickListener {
                //update Data.

                lifecycleScope.launch {
                    updateAbout(aboutResponse(editversion.text.toString(),editEmail.text.toString(),editNumber.text.toString(),editInfo.text.toString()))
                    Toast.makeText(context,"About Updated!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun getAbout(): Boolean {
        var id = userID.accountID
        lateinit var imageRef: List<imagePathResponse>
        Log.e("EDITPROFILE", id.toString())
        val response = try {
            retrofitInstance.api.getAbout()
        } catch (e: IOException) {
            Log.e("Profile", "IOException, you might not have internet connection")
            return false
        } catch (e: HttpException) {
            Log.e("Profile", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            aboutData = response.body()!!
        }

        return true
    }

    private suspend fun updateAbout(aboutResponse: aboutResponse): Boolean{
        var id = userID.accountID
        lateinit var imageRef: List<imagePathResponse>
        Log.e("EDITPROFILE", id.toString())
        val response = try {
            retrofitInstance.api.updateAbout(aboutResponse)
        } catch (e: IOException) {
            Log.e("Profile", "IOException, you might not have internet connection")
            return false
        } catch (e: HttpException) {
            Log.e("Profile", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            Log.e("SUCCESS", "200")
        }

        return true
    }
}

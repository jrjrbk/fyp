package com.example.testsem7v1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.example.testsem7v1.activity.userID
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.systemDatabase.aboutResponse
import com.example.testsem7v1.retrofit.systemDatabase.imagePathResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


class AboutFragment : Fragment() {

    private lateinit var aboutData: List<aboutResponse>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_about, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val aboutVersion: TextView = requireView().findViewById(R.id.aboutVersionInfo)
        val aboutEmail: TextView = requireView().findViewById(R.id.aboutContactEmailInfo)
        val aboutContact: TextView = requireView().findViewById(R.id.aboutContactNumberInfo)
        val aboutUs: TextView = requireView().findViewById(R.id.aboutUsInfo)

        lifecycleScope.launch {
            getAbout()

            aboutVersion.text = aboutData[0].aboutVersion
            aboutEmail.text = aboutData[0].aboutEmail
            aboutContact.text = aboutData[0].aboutNumber
            aboutUs.text = aboutData[0].aboutInfo
        }

    }

    private suspend fun getAbout(): Boolean{
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
}
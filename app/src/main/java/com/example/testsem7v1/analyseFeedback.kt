package com.example.testsem7v1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.activity.accessToken
import com.example.testsem7v1.adapter.analyseFeedbackAdapter
import com.example.testsem7v1.interfaces.onDeleteSongClickListener
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.systemDatabase.feedbackResponse
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class analyseFeedback : Fragment(),onDeleteSongClickListener {


    private lateinit var feedbackRV: RecyclerView
    private lateinit var feedbackAdapter: analyseFeedbackAdapter

    private lateinit var feedbackData: List<feedbackResponse>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_analyse_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRV()

        lifecycleScope.launch {
            getFeedback()
        }

    }

    private fun initRV(){
        feedbackRV = requireView().findViewById(R.id.analyseFeedbackRV)
        feedbackAdapter = analyseFeedbackAdapter()
        feedbackRV.adapter = feedbackAdapter
        feedbackRV.layoutManager = LinearLayoutManager(context)
        feedbackAdapter.listener = this
    }

    private suspend fun getFeedback():Boolean {
        val response = try {
            retrofitInstance.api.getFeedback()
        } catch (e: IOException) {
            Log.e("PLAYLISTITEM", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLISTITEM", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            feedbackData = response.body()!!
            feedbackAdapter.feedback = feedbackData
        }
        return true
    }

    override fun passData(data: String) {
        lifecycleScope.launch {
            getFeedback()
            feedbackAdapter.Refresh(feedbackData)
        }
    }
}
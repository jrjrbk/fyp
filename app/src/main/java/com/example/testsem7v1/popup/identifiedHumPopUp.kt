package com.example.testsem7v1.popup

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.R
import com.example.testsem7v1.activity.ACRHumResponse
import com.example.testsem7v1.adapter.identifiedHumAdapter
import com.example.testsem7v1.adapter.playlistPopupAdapter

class identifiedHumPopUp : DialogFragment() {


    lateinit var identifiedHumRV: RecyclerView
    lateinit var identifiedHumAdapter: identifiedHumAdapter

    override fun onStart() {
        super.onStart()
        val width = 450
        val height = 500
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setGravity(Gravity.CENTER)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_identified_hum_pop_up, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRV()
        identifiedHumAdapter.acrData = ACRHumResponse.metadata.humming
    }

    private fun initRV(){
        identifiedHumRV = requireView().findViewById(R.id.identifiedHumRV)
        identifiedHumAdapter = identifiedHumAdapter()
        identifiedHumRV.adapter = identifiedHumAdapter
        identifiedHumRV.layoutManager = LinearLayoutManager(context)

    }
}
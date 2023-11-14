package com.example.testsem7v1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.testsem7v1.databinding.FragmentFeedbackBinding
import com.example.testsem7v1.retrofit.feedbackData
import com.example.testsem7v1.retrofit.retrofitInstance
import retrofit2.HttpException
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FeedbackFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FeedbackFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentFeedbackBinding.inflate(layoutInflater)
        return inflater.inflate(R.layout.fragment_feedback, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spinner: Spinner = view.findViewById(R.id.feedbackSpinner)
        val spinnerArray = {"toli";"toli2";"toli3"}
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val comments: EditText = view.findViewById(R.id.commentEditText)
        val submitButton: Button = view.findViewById(R.id.submitButton)
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            view.context,
            R.array.feedback_array,
            R.layout.spinner_item_feedback          // spinner layout.
        ).also { adapter ->
            // Specify the layout to use when the list of choices appear.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter


//            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
//                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
//                    TODO("Not yet implemented")
//                }
//
//                override fun onNothingSelected(p0: AdapterView<*>?) {
//                    TODO("Not yet implemented")
//                }
//
//            }
        }

        submitButton.setOnClickListener{
            val rating = ratingBar.rating
            val comments = comments.text.toString().trim()
            val type = spinner.selectedItem.toString()
            val uID = userID.accountID

            Log.e("FeedBACK", "rating $rating, type: $type, comments $comments, accountID ${userID.accountID}")

            var feedbackData= feedbackData(
                feedbackType = type,
                AccountID = uID,
                comments = comments,
                rating = rating
            )

            lifecycleScope.launchWhenCreated {
                val response = try{
                    retrofitInstance.api.userRate(feedbackData)
                } catch (e: IOException){
                    Log.e("MainActivity", "IOException, you might not have internet connection")
                    Log.e("Error",throw(e))
                    return@launchWhenCreated // so that the thread can resume.
                } catch (e: HttpException){
                    Log.e("MainActivity", "HttpException, unexpected response")
                    return@launchWhenCreated // so that the thread can resume.
                }

                if (response.isSuccessful && response.body() != null){
                    Log.e("Feedback", "Response successful CODE:${response.code()}")
                    Toast.makeText(this@FeedbackFragment.context, "Feedback sent", Toast.LENGTH_LONG).show()
                }else{
                    Log.e("Feedback", "Response not successful")
                }
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FeedbackFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FeedbackFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}


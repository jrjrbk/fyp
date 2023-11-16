package com.example.testsem7v1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.adapter.recyclerAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var adapter: recyclerAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var dataArrayList: ArrayList<Test>

    lateinit var imageId: Array<Int>
    lateinit var heading: Array<String>
    lateinit var news: Array<String>

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
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataInitialize()
        val layoutManager = LinearLayoutManager(context)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = layoutManager
        // call the method addItemDecoration with the
        // recyclerView instance and add default Item divider
        recyclerView.addItemDecoration(
            //This one instead of passing (this/context, *) i passed this.requireContext
            // Because this is in a fragment not the main activity.
            RecyclerItemDecoration(this.requireContext(), R.drawable.divider)
        )
        recyclerView.setHasFixedSize(true)
        adapter = recyclerAdapter(dataArrayList)
        recyclerView.adapter = adapter
    }

    private fun dataInitialize(){

        dataArrayList = arrayListOf<Test>()

        imageId = arrayOf(
            R.drawable.baseline_edit_24,
            R.drawable.baseline_tag_faces_24,
            R.drawable.baseline_search_24,
            R.drawable.baseline_settings_24
        )

        heading = arrayOf(
            "Edit Profile",
            "Give Feedback",
            "Sync",
            "Settings"
        )

        for (i in imageId.indices){
            val test = Test(imageId[i], heading[i])
            dataArrayList.add(test)
        }
    }


}
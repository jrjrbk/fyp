package com.example.testsem7v1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.databinding.FragmentSearchBinding
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.spotify.album.albumSearchAdapter
import okio.IOException
import retrofit2.HttpException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //For spotify search
    private val limit = 10
    private val offset = 0

    // recycler view
    private lateinit var albumSearchAdapter: albumSearchAdapter
    private lateinit var binding: FragmentSearchBinding


    lateinit var recyclerViewSearch: RecyclerView
    lateinit var searchView: SearchView
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
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchView = requireView().findViewById(R.id.searchView)
        //Spinner
        val spinner: Spinner = view.findViewById(R.id.searchSpinner)
        ArrayAdapter.createFromResource(
            view.context,
            R.array.search_array,
            android.R.layout.simple_spinner_item
        ).also{ adapter ->
            //Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            //Apply the adapter to the spinner
            spinner.adapter = adapter
        }
        albumSearchAdapter = albumSearchAdapter()
        recyclerViewSearch = view.findViewById(R.id.searchRV)
        recyclerViewSearch.adapter=albumSearchAdapter
//        recyclerViewSearch.setHasFixedSize(true)
        recyclerViewSearch.layoutManager = LinearLayoutManager(this.context)

        binding =FragmentSearchBinding.inflate(layoutInflater)
//        setupRecyclerView()


        //queryTextSubmit -> q
        // type -> will add later
        // limit and offset -> 10 and 0?

        //Search
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.e("SearchTAG", query.toString())
                Log.e("SearchTAG",spinner.selectedItem.toString())

                lifecycleScope.launchWhenCreated {
                    val response = try{
                        val authTest = "${accessToken.token_type} ${accessToken.access_token}"
                        retrofitInstance.api.searchAlbumSpotify(authHeader = authTest,q=query.toString(),type=spinner.selectedItem.toString().decapitalize(),limit=20,offset=offset)
                    } catch (e: IOException){
                        Log.e("SEARCHFRAGMENT", "IOException, you might not have internet connection")
                        Log.e("SEARCHFRAGMENT",throw(e))
                        return@launchWhenCreated // so that the thread can resume.
                    } catch (e: HttpException){
                        Log.e("SEARCHFRAGMENT", "HttpException, unexpected response")
                        return@launchWhenCreated // so that the thread can resume.
                    }

                    if(response.isSuccessful && response.body() != null){
                        val albumItems = response.body()

                        albumSearchAdapter.albums = albumItems!!.albums.items
                        //problem is response body contains (album)-> (Albums) -> (Item)
                        Log.e("SEARCHFRAGMENT", "Response successful CODE:${response.code()}")

                    }
                    else{
                        Log.e("SEARCHFRAGMENT", "Response not successful")
                    }

                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })
    }

//    private fun setupRecyclerView() = binding.searchRV.apply {
//        adapter = albumSearchAdapter //adapter from the RecyclerViewBinding.
//        layoutManager = LinearLayoutManager(this@SearchFragment.context)
//    }


}
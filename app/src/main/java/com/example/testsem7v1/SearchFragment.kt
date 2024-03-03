package com.example.testsem7v1

import android.app.DownloadManager.Query
import android.app.SearchManager
import android.content.ComponentName
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.SearchRecentSuggestionsProvider
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.provider.SearchRecentSuggestions
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.SimpleCursorAdapter
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContentResolverCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.cursoradapter.widget.CursorAdapter
import androidx.lifecycle.lifecycleScope
import androidx.loader.content.CursorLoader
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.activity.accessToken
import com.example.testsem7v1.activity.loginSession
import com.example.testsem7v1.activity.userID
import com.example.testsem7v1.databinding.FragmentSearchBinding
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.adapter.albumSearchAdapter
import com.example.testsem7v1.adapter.artistSearchAdapter
import com.example.testsem7v1.adapter.trackSearchAdapter
import com.example.testsem7v1.interfaces.optionItemClickListener
import com.example.testsem7v1.interfaces.spotifyItemClickListener
import com.example.testsem7v1.popup.clickOptionPopUp
import com.example.testsem7v1.retrofit.systemDatabase.QueryResponse
import com.example.testsem7v1.retrofit.systemDatabase.queryData
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException


class SearchFragment : Fragment(), spotifyItemClickListener, optionItemClickListener {


    //For spotify search
    private val limit = 10
    private val offset = 0

    // recycler view
    //Album
    private lateinit var albumSearchAdapter: albumSearchAdapter
    private lateinit var binding: FragmentSearchBinding
    //Artist
    private lateinit var artistSearchAdapter: artistSearchAdapter
    private lateinit var trackSearchAdapter: trackSearchAdapter


    lateinit var recyclerViewSearch: RecyclerView
    lateinit var searchView: SearchView

    private lateinit var queryData: List<QueryResponse>



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

        val cursor = MatrixCursor(arrayOf("_id","data"))

        cursor.addRow(arrayOf("1","log in for search history"))
        searchView.suggestionsAdapter = testAdapter(requireContext(),cursor)


        //Spinner
        val chipGroup: ChipGroup = view.findViewById(R.id.chipGroup)
        var checkedItem = "album"

        chipGroup.setOnCheckedStateChangeListener { group, checkedID ->
            for (i in checkedID) {
                val chip: Chip? = view.findViewById(i)
                checkedItem = chip!!.text.toString().decapitalize()
                Log.e("CHIP", checkedItem)
            }

        }


        binding = FragmentSearchBinding.inflate(layoutInflater)

        searchView.setOnSearchClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                Log.e("Test","clicked")

            }
        })

        searchView.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                Log.e("Test","clicked")
            }
        })


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                Log.e("SearchTAG", query.toString())
                //Submitted item will be saved to query database

                //saveQuery(query.toString())

                lifecycleScope.launch {
                    if(loginSession){
                        saveQuery(query.toString())
                    }

                    if(checkedItem == "album"){
                        initAlbumRV()
                        searchAlbum(query!!, checkedItem)}
                    else if(checkedItem == "artist"){
                        initArtistRV()
                        searchArtist(query!!, checkedItem)}
                    else if(checkedItem == "track"){
                        initTrackRV()
                        searchTrack(query!!, checkedItem)}
                }
                return true
            }


            override fun onQueryTextChange(newText: String?): Boolean {

                Log.e("SearchFragment",newText.toString())
                // Ill implement this if im not lazy, what's important is the query history.
                val cursor = MatrixCursor(arrayOf("_id","data"))


                // newText has string in cursor
                // addArray of
                if(loginSession){
                    lifecycleScope.launch {
                        getQuery()
                        queryData.forEachIndexed{ index, value ->
                            Log.e("foreach", "$index : $value")
                            cursor.addRow(arrayOf("${index+1}", value.query))
                        }
                        searchView.suggestionsAdapter.changeCursor(cursor)
                    }
                }

                return false
            }



        })
    }

    private suspend fun searchAlbum(query: String, type: String): Boolean{
        val response = try {
            val authTest = "${accessToken.token_type} ${accessToken.access_token}"
            retrofitInstance.api.searchAlbumSpotify(
                authHeader = authTest,
                q = query.toString(),
                type = type,
                limit = 20,
                offset = offset
            )
        } catch (e: IOException) {
            Log.e(
                "SEARCHFRAGMENT",
                "IOException, you might not have internet connection"
            )
            Log.e("SEARCHFRAGMENT", throw (e))
            return false // so that the thread can resume.
        } catch (e: HttpException) {
            Log.e("SEARCHFRAGMENT", "HttpException, unexpected response")
            return false // so that the thread can resume.
        }

        if (response.isSuccessful && response.body() != null) {
            val albumItems = response.body()

            albumSearchAdapter.albums = albumItems!!.albums.items
            //problem is response body contains (album)-> (Albums) -> (Item)
            Log.e("SEARCHFRAGMENT", "Response successful CODE:${response.code()}")

        } else {
            Log.e("SEARCHFRAGMENT", "Response not successful")
        }

        return true
    }

    private suspend fun searchArtist(query: String, type: String): Boolean{
        val response = try {
            val authTest = "${accessToken.token_type} ${accessToken.access_token}"
            retrofitInstance.api.searchArtistSpotify(
                authHeader = authTest,
                q = query.toString(),
                type = type,
                limit = 20,
                offset = offset
            )
        } catch (e: IOException) {
            Log.e(
                "SEARCHFRAGMENT",
                "IOException, you might not have internet connection"
            )
            Log.e("SEARCHFRAGMENT", throw (e))
            return false // so that the thread can resume.
        } catch (e: HttpException) {
            Log.e("SEARCHFRAGMENT", "HttpException, unexpected response")
            return false // so that the thread can resume.
        }

        if (response.isSuccessful && response.body() != null) {
            val artistItems = response.body()

            artistSearchAdapter.artist = artistItems!!.artists.items
            //problem is response body contains (album)-> (Albums) -> (Item)
            Log.e("SEARCHFRAGMENT", "Response successful CODE:${response.code()}")

        } else {
            Log.e("SEARCHFRAGMENT", "Response not successful")
        }

        return true
    }

    private suspend fun searchTrack(query: String, type: String): Boolean{
        val response = try {
            val authTest = "${accessToken.token_type} ${accessToken.access_token}"
            retrofitInstance.api.searchTrackSpotify(
                authHeader = authTest,
                q = query.toString(),
                type = type,
                limit = 20,
                offset = offset
            )
        } catch (e: IOException) {
            Log.e(
                "SEARCHFRAGMENT",
                "IOException, you might not have internet connection"
            )
            Log.e("SEARCHFRAGMENT", throw (e))
            return false // so that the thread can resume.
        } catch (e: HttpException) {
            Log.e("SEARCHFRAGMENT", "HttpException, unexpected response")
            return false // so that the thread can resume.
        }

        if (response.isSuccessful && response.body() != null) {
            val trackItems = response.body()
            trackSearchAdapter.tracks = trackItems!!.tracks.items
            Log.e("SEARCHFRAGMENT", "Response successful CODE:${response.code()}")

        } else {
            Log.e("SEARCHFRAGMENT", "Response not successful")
        }

        return true
    }

    private fun initAlbumRV(){
        albumSearchAdapter = albumSearchAdapter()
        recyclerViewSearch = requireView().findViewById(R.id.searchRV)
        recyclerViewSearch.adapter = albumSearchAdapter
        recyclerViewSearch.layoutManager = GridLayoutManager(this@SearchFragment.context,2, GridLayoutManager.VERTICAL,false)
        albumSearchAdapter.listener = this
    }

    private fun initArtistRV(){
        artistSearchAdapter = artistSearchAdapter()
        recyclerViewSearch = requireView().findViewById(R.id.searchRV)
        recyclerViewSearch.adapter = artistSearchAdapter
        recyclerViewSearch.layoutManager = GridLayoutManager(this@SearchFragment.context,2, GridLayoutManager.VERTICAL,false)
//        recyclerViewSearch.layoutManager = LinearLayoutManager(this.context)
        artistSearchAdapter.listener = this
    }

    private fun initTrackRV(){
        trackSearchAdapter = trackSearchAdapter()
        recyclerViewSearch = requireView().findViewById(R.id.searchRV)
        recyclerViewSearch.adapter = trackSearchAdapter
        recyclerViewSearch.layoutManager = GridLayoutManager(this@SearchFragment.context,2, GridLayoutManager.VERTICAL,false)
//        recyclerViewSearch.layoutManager = LinearLayoutManager(this.context)
        trackSearchAdapter.listener = this

    }

    override fun passTrackRef(id: String) {
        val popup = clickOptionPopUp()
        val bundle = Bundle()
        bundle.putString("SpotifyRef",id)
        bundle.putString("Type","Track")
        popup.arguments = bundle
        popup.listener = this
        popup.show(requireActivity().supportFragmentManager, "option")
    }

    override fun passAlbumRef(id: String) {
        val popup = clickOptionPopUp()
        val bundle = Bundle()
        bundle.putString("SpotifyRef",id)
        bundle.putString("Type","Album")
        popup.arguments = bundle
        popup.listener = this
        popup.show(requireActivity().supportFragmentManager, "option")
    }

    override fun passArtistRef(id: String) {
        super.passArtistRef(id)
        val aboutArtistFragment = aboutArtistFragment()
        val bundle = Bundle()
        bundle.putString("artistRef",id)
        aboutArtistFragment.arguments = bundle

        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, aboutArtistFragment).addToBackStack("artist").commit()
    }

    override fun viewAlbumData(id: String) {
        super.viewAlbumData(id)

        val viewAlbum = viewAlbum()
        val bundle = Bundle()
        bundle.putString("albumRef",id)
        viewAlbum.arguments = bundle

        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, viewAlbum).addToBackStack("album").commit()
    }

    override fun viewArtistData(id: String) {
        val aboutArtistFragment = aboutArtistFragment()
        val bundle = Bundle()
        bundle.putString("artistRef",id)
        aboutArtistFragment.arguments = bundle

        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, aboutArtistFragment).addToBackStack("artist").commit()
    }

    private suspend fun getQuery():Boolean {
        val response = try {
            retrofitInstance.api.getQuery(userID.accountID)
        } catch (e: IOException) {
            Log.e("PLAYLISTITEM", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLISTITEM", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            queryData = response.body()!!
        }
        return true
    }

    private suspend fun saveQuery(query:String):Boolean {
        val response = try {
            retrofitInstance.api.saveQuery(queryData(userID.accountID,query))
        } catch (e: IOException) {
            Log.e("PLAYLISTITEM", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLISTITEM", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            Log.e("PLAYLISTITEM", "NOICE")
        }
        return true
    }


}


class testAdapter(context: Context, cursor: Cursor): CursorAdapter(context,cursor) {
    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        return LayoutInflater.from(context).inflate(R.layout.suggestion_item_layout, parent, false)
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val nameTextView = view!!.findViewById<TextView>(R.id.item_label)
        val name = cursor?.getString(1)
        nameTextView.text = name

    }


}




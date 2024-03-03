package com.example.testsem7v1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.activity.prev_menuID
import com.example.testsem7v1.activity.userID
import com.example.testsem7v1.adapter.mainPlaylistAdapter
import com.example.testsem7v1.interfaces.OnPlaylistItemClickListener
import com.example.testsem7v1.popup.addPlaylistListener
import com.example.testsem7v1.popup.playlistAddPopup
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.systemDatabase.playlistDataItem
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlaylistFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlaylistFragment : Fragment(), OnPlaylistItemClickListener, addPlaylistListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    // RecyclerView
    private lateinit var mainPlaylistAdapter: mainPlaylistAdapter
    private lateinit var recyclerViewPlaylist: RecyclerView

    private var passedData = 0
    private lateinit var updatedList: List<playlistDataItem>
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
        return inflater.inflate(R.layout.fragment_playlist, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addButton: Button = requireView().findViewById(R.id.addPlaylist)
        initPlaylistRV()

        // RecyclerView
        lifecycleScope.launchWhenCreated {
            getPlaylist()
        }

        // Add Playlist
        addButton.setOnClickListener{
            val popup = playlistAddPopup()
            // listener for addPlaylisthere
            popup.listener = this
            popup.show(requireActivity().supportFragmentManager, "popup")
        }

        mainPlaylistAdapter.listener = this

    }


    private fun initPlaylistRV(){
        mainPlaylistAdapter = mainPlaylistAdapter()
        recyclerViewPlaylist = requireView().findViewById(R.id.playlistRV)
        recyclerViewPlaylist.adapter = mainPlaylistAdapter
        recyclerViewPlaylist.layoutManager = LinearLayoutManager(context)
    }

    private suspend fun getPlaylist(): Boolean{

        var id = userID.accountID
        val response = try {
            retrofitInstance.api.getPlaylist(id)
        } catch (e: IOException) {
            Log.e("PLAYLIST-GETGOOD", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLIST-GETGOOD", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            mainPlaylistAdapter.mainPlaylist = response.body()!!
        }

        return true
    }

    private suspend fun updatePlaylist(): Boolean{

        var id = userID.accountID
        val response = try {
            retrofitInstance.api.getPlaylist(id)
        } catch (e: IOException) {
            Log.e("PLAYLIST-GETGOOD", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLIST-GETGOOD", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            updatedList = response.body()!!
        }

        return true
    }

    override fun passData(data: Int) {
        Log.e("INTERFACETEST","I got $data")
        passedData = data


        //Initialize Fragment
        val playlistItemFragment = playlistItemFragment()
        //Creates bundle to pass to fragment
        val bundle = Bundle()
        bundle.putString("playlistID", passedData.toString())
        playlistItemFragment.arguments = bundle


        //Replaces fragment
        val fragmentManager = activity?.supportFragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        fragmentTransaction.replace(R.id.frameLayout, playlistItemFragment).addToBackStack("playList").commit()



        // Check this later, for improving the navigation outside the 5 main fragments.
        // need to modify main_activity var
        // prev_menuID & selected_item needs to be global in the activity.
//        prev_menuID = R.id.playlistItem

    }

    override fun onAddPlaylist() {
        lifecycleScope.launch {
            Log.e("RESUMED","GETTING PLAYLIST")
            updatePlaylist()
            mainPlaylistAdapter.Refresh(updatedList)
        }
    }


}
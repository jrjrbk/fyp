package com.example.testsem7v1

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.activity.admin
import com.example.testsem7v1.activity.userID
import com.example.testsem7v1.adapter.recyclerAdapter
import com.example.testsem7v1.retrofit.systemDatabase.Username
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.systemDatabase.imagePathResponse
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val joinedDate: TextView = requireView().findViewById(R.id.joinedDate)
        // if admin.
        // initialize this data if not admin.
        if(admin){
            dataAdminInitialize()
            joinedDate.text = "Admin Priveleges"
        } else{
            dataInitialize()

        }
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

        //image
        val img: ImageView = view.findViewById(R.id.profileImage)
        img.clipToOutline = true
        lifecycleScope.launch {
            Picasso.get().load(getImage()).into(img)
        }

//        Image File Checker (Unused)
//        var imgFile: File = File("A:/UMS/Sem7/FYP/database/img/harambe.jpg")
//
//        println(imgFile.exists())
//        if (imgFile.exists()) {
//            var bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
//
//            val img: ImageView = view.findViewById(R.id.profileImage)
//
//            img.setImageBitmap(bitmap)
//        }
    }

    private fun dataInitialize() {

        dataArrayList = arrayListOf<Test>()

        var username: TextView = requireView().findViewById(R.id.profileUsername)

        lifecycleScope.launch {
            username.text = getUsername()
        }

        Log.e("PROFILEUSERID", userID.accountID.toString())
        imageId = arrayOf(
            R.drawable.baseline_edit_24,
            R.drawable.baseline_tag_faces_24,
//            R.drawable.baseline_search_24,
            R.drawable.baseline_info_24,
            R.drawable.baseline_logout_24
        )

        heading = arrayOf(
            "Edit Profile",
            "Give Feedback",
            "About",
            "Log Out"
        )

        for (i in imageId.indices) {
            val test = Test(imageId[i], heading[i])
            dataArrayList.add(test)
        }
    }

    private fun dataAdminInitialize() {

        dataArrayList = arrayListOf<Test>()

        var username: TextView = requireView().findViewById(R.id.profileUsername)

        lifecycleScope.launch {
            username.text = getUsername()
        }

        Log.e("PROFILEUSERID", userID.accountID.toString())
        imageId = arrayOf(
            R.drawable.baseline_edit_24,
            R.drawable.baseline_tag_faces_24,
//            R.drawable.baseline_search_24,
            R.drawable.baseline_info_24,
            R.drawable.baseline_logout_24
        )

        heading = arrayOf(
            "Edit Profile",
            "Analyse Feedback",
            "Edit About",
            "Log Out"
        )

        for (i in imageId.indices) {
            val test = Test(imageId[i], heading[i])
            dataArrayList.add(test)
        }
    }

    private suspend fun getUsername(): String {

        lateinit var username: Username
        var id = userID.accountID
        Log.e("PROFILETAG", id.toString())
        val response = try {
            retrofitInstance.api.getUsernameByID(id)
        } catch (e: IOException) {
            Log.e("Profile", "IOException, you might not have internet connection")
            return ""

        } catch (e: HttpException) {
            Log.e("Profile", "HttpException, unexpected response")
            return ""
        }
        if (response.isSuccessful && response.body() != null) {
            username = response.body()!!
        }

        return username.username

    }

    private suspend fun getImage(): String {

        var id = userID.accountID
        lateinit var imageRef: List<imagePathResponse>
        Log.e("EDITPROFILE", id.toString())
        val response = try {
            retrofitInstance.api.getImage(id)
        } catch (e: IOException) {
            Log.e("Profile", "IOException, you might not have internet connection")
            return ""

        } catch (e: HttpException) {
            Log.e("Profile", "HttpException, unexpected response")
            return ""
        }
        if (response.isSuccessful && response.body() != null) {
            imageRef = response.body()!!
        }
        if(imageRef.isEmpty()){
            return "example"
        }
        return imageRef[0].imagePath
    }

}

package com.example.testsem7v1.popup

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.testsem7v1.R
import com.example.testsem7v1.interfaces.onDoneListener
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.systemDatabase.playlistDataItem
import com.example.testsem7v1.retrofit.systemDatabase.updatePlaylistData
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import java.io.InputStream
import java.nio.file.Paths


class updatePlaylistPopup : DialogFragment() {

    private lateinit var playlistData: List<playlistDataItem>
    private val GREQUESTCODE = 1000
    private var realFilePath = ""

    private var imgName = ""

    private lateinit var progressBar: ProgressBar

    lateinit var listener: onDoneListener

    private lateinit var image: ImageView


    override fun onStart() {
        super.onStart()
        val width = 600
        val height = 700
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setGravity(Gravity.CENTER)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_playlist_popup, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var playlistID = requireArguments().getString("playlistID")!!.toInt()

        val name: EditText = requireView().findViewById(R.id.updateOptionNameInput)
        image = requireView().findViewById(R.id.updateOptionPopupImage)
        val changeImage: TextView = requireView().findViewById(R.id.updateOptionChangeImage)
        val button: Button = requireView().findViewById(R.id.updateOptionButton)
        progressBar = requireView().findViewById(R.id.updateOptionBar)

        lifecycleScope.launch {
            getPlaylistData(playlistID)

            name.setText(playlistData[0].playlistName)
            Picasso.get().load(playlistData[0].playlistImage).into(image)
            imgName = playlistData[0].playlistImage

            changeImage.setOnClickListener {
                openGallery()
            }


            button.setOnClickListener{
                if(!progressBar.isVisible) {
                    lifecycleScope.launch {
                        updatePlaylist(playlistID, name.text.toString(), imgName)
                        dismiss()
                        listener.done()
                    }
                }
            }

        }
    }

    private fun openGallery(): Boolean{
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GREQUESTCODE)
        return true
    }





    private suspend fun getPlaylistData(playlistID: Int):Boolean {
        val response = try {
            retrofitInstance.api.getSpecificPlaylist(playlistID)
        } catch (e: IOException) {
            Log.e("PLAYLISTITEM", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLISTITEM", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            playlistData = response.body()!!
        }
        return true
    }

    private suspend fun updatePlaylist(playlistID: Int, playlistName: String, playlistImage:String):Boolean {
        val response = try {
            var updateData = updatePlaylistData(playlistName,playlistImage)
            retrofitInstance.api.updatePlaylist(playlistID,updateData)
        } catch (e: IOException) {
            Log.e("PLAYLISTITEM", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("PLAYLISTITEM", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            Log.e("PLAYLISTITEM", "Successful!")
        }
        return true
    }


    @Deprecated("deprecated but not for 13 hehe")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        lateinit var selectedImageUri: Uri
        if(requestCode==GREQUESTCODE && resultCode == Activity.RESULT_OK && data !=null){

            selectedImageUri= data.data!!
            val projection = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = requireActivity().contentResolver.query(selectedImageUri,projection,null,null,null)
            val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            val filePath = cursor.getString(columnIndex)

            val fileName = filePath.substring(filePath.lastIndexOf("/")+1)

            cursor.close()
            realFilePath = filePath

            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    if(realFilePath.isNotEmpty()) {
                        progressBar.isVisible = true
                        val lolz: InputStream =
                            requireContext().resources.openRawResource(R.raw.credential)

                        val objectName = "playlist_img/" + fileName
                        val bucketName = "fyp_images_for_shabam"

                        val testStorage =
                            StorageOptions.newBuilder().setProjectId("fyp-project-410518")
                                .setCredentials(ServiceAccountCredentials.fromStream(lolz))
                                .build().service
                        val bucket = testStorage.get(
                            bucketName,
                            Storage.BucketGetOption.fields(Storage.BucketField.DEFAULT_OBJECT_ACL)
                        )
                        Log.e("BUCKET", bucket.name)

                        //Now upload Image
                        val blobId =
                            BlobId.of(bucketName, objectName)
                        val blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build()

                        // Precondition, if file exists

                        if(testStorage[bucketName, objectName] != null){
                            this@updatePlaylistPopup.requireActivity().runOnUiThread {
                                progressBar.isVisible = false
                                Picasso.get().load(imgName).into(image)
                            }
                            imgName = "https://storage.googleapis.com/fyp_images_for_shabam/playlist_img/"+fileName
                            Log.e("FILEEXIST","FILE ALREADY EXIST YOU DUMMY")
                        }

                        else{
                            val precondition: Storage.BlobWriteOption
                            precondition =
                                if (testStorage[bucketName, objectName] == null) {
                                    Storage.BlobWriteOption.doesNotExist()
                                }
                                else {
                                    Storage.BlobWriteOption.generationMatch()
                                }

                            Log.e("FILEPATH", realFilePath)
                            Log.e("FILENAME", fileName)

                            val filePath = realFilePath
                            testStorage.createFrom(blobInfo, Paths.get(filePath), precondition)
                            // Show Uploading Status
                            // Set directory of PUBLIC URL of PIC to database.
                            println(
                                "File $filePath uploaded to bucket fyp_images_for_shazam as $objectName"
                            )
                            imgName = "https://storage.googleapis.com/fyp_images_for_shabam/playlist_img/"+fileName
                            this@updatePlaylistPopup.requireActivity().runOnUiThread {
                                progressBar.isVisible = false
                                Picasso.get().load(imgName).into(image)
                                Toast.makeText(context,"Photos uploaded!", Toast.LENGTH_SHORT).show()
                            }
                        }


                    }
                } catch (e: java.io.IOException) {
                    e.printStackTrace()
                }

            }
        }


    }

}
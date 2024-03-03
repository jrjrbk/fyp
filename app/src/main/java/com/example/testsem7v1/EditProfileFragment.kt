package com.example.testsem7v1

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.testsem7v1.activity.userID
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.systemDatabase.Username
import com.example.testsem7v1.retrofit.systemDatabase.imagePath
import com.example.testsem7v1.retrofit.systemDatabase.imagePathResponse
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.io.InputStream
import java.nio.file.Paths


class EditProfileFragment : Fragment() {

    lateinit var editProfileImage: ImageView
    lateinit var editProfileName: EditText
    lateinit var editProfileImgButton: TextView
    lateinit var editProfileConfirm: Button

    private val GREQUESTCODE = 1000
    private var realFilePath = ""
    private var imgName = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editProfileImage = requireView().findViewById(R.id.editProfileImg)
        editProfileName = requireView().findViewById(R.id.editProfileUsernameEdit)
        editProfileImgButton = requireView().findViewById(R.id.editProfileEdit)
        editProfileConfirm = requireView().findViewById(R.id.editProfileConfirmButton)

        editProfileImage.clipToOutline=true
        lifecycleScope.launch {
            imgName= getImage()
            Picasso.get().load(imgName).into(editProfileImage)
            editProfileName.setText(getUsername())

            editProfileImgButton.setOnClickListener {
                openGallery()
            }

            editProfileConfirm.setOnClickListener {
                // check if images is uploading if not.

                // do this
                // username update and image update.
                lifecycleScope.launch {
                    updateUsername(editProfileName.text.toString())
                    //update image
                    updateImage(imgName)
                }
            }
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

    private suspend fun updateUsername(username: String): String {

        val username = Username(username)
        var id = userID.accountID

        Log.e("PROFILETAG", id.toString())
        val response = try {
            retrofitInstance.api.updateUsername(id,username)
        } catch (e: IOException) {
            Log.e("Profile", "IOException, you might not have internet connection")
            return ""

        } catch (e: HttpException) {
            Log.e("Profile", "HttpException, unexpected response")
            return ""
        }
        if (response.isSuccessful && response.body() != null) {
            Log.e("EditProfile", "Updated Username")
        }

        return username.username
    }

    private suspend fun updateImage(imageRef: String): String {

        var id = userID.accountID
        Log.e("EDITPROFILE", id.toString())
        val response = try {
            retrofitInstance.api.updateImage(id, imagePath(imageRef))
        } catch (e: IOException) {
            Log.e("Profile", "IOException, you might not have internet connection")
            return ""

        } catch (e: HttpException) {
            Log.e("Profile", "HttpException, unexpected response")
            return ""
        }
        if (response.isSuccessful && response.body() != null) {
            Log.e("EditProfile", "Updated Username")
        }
        return "yes"
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


    // Images Upload
    private fun openGallery(): Boolean{
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GREQUESTCODE)
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
//                    progressBar.isVisible = true
                        val lolz: InputStream =
                            requireContext().resources.openRawResource(R.raw.credential)

                        val objectName = "profile_img/" + fileName
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
                            this@EditProfileFragment.requireActivity().runOnUiThread {
//                            progressBar.isVisible = false
                                Picasso.get().load(imgName).into(editProfileImage)
                            }
                            imgName = "https://storage.googleapis.com/fyp_images_for_shabam/profile_img/"+fileName
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
                            imgName = "https://storage.googleapis.com/fyp_images_for_shabam/profile_img/"+fileName
                            this@EditProfileFragment.requireActivity().runOnUiThread {
//                            progressBar.isVisible = false
                                Picasso.get().load(imgName).into(editProfileImage)
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
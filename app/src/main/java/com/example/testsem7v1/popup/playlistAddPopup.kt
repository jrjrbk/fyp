package com.example.testsem7v1.popup

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.testsem7v1.PlaylistFragment
import com.example.testsem7v1.R
import com.example.testsem7v1.activity.userID
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.systemDatabase.addPlaylistData
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.time.LocalDate

class playlistAddPopup : DialogFragment() {

    lateinit var listener: addPlaylistListener

    override fun onStart() {
        super.onStart()
        val width = 650
        val height = 500
        dialog?.window?.setLayout(width, height)
        dialog?.window?.setGravity(Gravity.CENTER)
        dialog?.window?.setDimAmount(0.0F)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.popup_playlist, container, false)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cancelButton: Button = requireView().findViewById(R.id.cancelPlaylist)
        val addButton: Button = requireView().findViewById(R.id.addPlaylist)
        val playlistName: EditText = requireView().findViewById(R.id.playlistNameEditText)

        cancelButton.setOnClickListener {
            dismiss()
        }

        addButton.text="Skip"
        playlistName.doOnTextChanged { text, start, before, count ->

            if(text!!.trim().isNotEmpty()){
                addButton.text="Add"
            }
            else if(text!!.isEmpty()){
                addButton.text="Skip"
            }
        }

        addButton.setOnClickListener{
            val name = playlistName.text.toString()

            if(name.trim().isEmpty()){
                dismiss()
            }

            lifecycleScope.launch {
                addPlaylist(name)
                dismiss()
                Toast.makeText(context,"Playlist Added", Toast.LENGTH_SHORT).show()
                listener.onAddPlaylist()
            }
        }

    }

    private suspend fun addPlaylist(name:String): Boolean {

        var id = userID.accountID
        var addPlaylistData = addPlaylistData(playlistName = name, playlistDate = LocalDate.now().toString(), accountID = id)
        val response = try {
            retrofitInstance.api.addPlaylist(addPlaylistData)
        } catch (e: IOException) {
            Log.e("Playlist", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("Playlist", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            Log.e("Playlist", response.body().toString())
        }

        return true
    }
}

interface addPlaylistListener{
    fun onAddPlaylist()
}
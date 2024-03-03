package com.example.testsem7v1.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.databinding.ItemAnalysefeedbackBinding
import com.example.testsem7v1.databinding.PlaylistItemBinding
import com.example.testsem7v1.interfaces.OnPlaylistItemClickListener
import com.example.testsem7v1.interfaces.onDeleteSongClickListener
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.spotify.getSeveralTrack.Track
import com.example.testsem7v1.retrofit.systemDatabase.feedbackData
import com.example.testsem7v1.retrofit.systemDatabase.feedbackResponse
import com.example.testsem7v1.retrofit.systemDatabase.playlistDataItem
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class analyseFeedbackAdapter: RecyclerView.Adapter<analyseFeedbackAdapter.ViewHolder>(){

    lateinit var listener: onDeleteSongClickListener

    //    init {
//        this.listener =
//    }
    class ViewHolder (val binding: ItemAnalysefeedbackBinding): RecyclerView.ViewHolder(binding.root) {

    }

    private val diffCallBack = object: DiffUtil.ItemCallback<feedbackResponse>(){
        override fun areItemsTheSame(
            oldItem: feedbackResponse,
            newItem: feedbackResponse
        ): Boolean {
            return oldItem.feedbackID == newItem.feedbackID
        }

        override fun areContentsTheSame(
            oldItem: feedbackResponse,
            newItem: feedbackResponse
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this,diffCallBack)
    var feedback: List<feedbackResponse>
        get() = differ.currentList
        set(value){differ.submitList(value)}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAnalysefeedbackBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return feedback.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply{
            val feedback = feedback[position]
            feedbackType2.text = feedback.feedbackType
            ratingAnalyse.text = feedback.rating.toString()
            feedbackComment2.text = feedback.comments
            feedbackNo.text = (position+1).toString()

             deleteFeedback.setOnClickListener (object: View.OnClickListener{
                override fun onClick(v: View?) {
                    val activity: AppCompatActivity = v!!.context as AppCompatActivity

                    activity.lifecycleScope.launch {
                        deleteFeedback(feedback.feedbackID)
                        //refresh
                        listener.passData("Deleted")
                    }
                }

            })

        }
    }

    private suspend fun deleteFeedback(feedbackID: Int): Boolean{

        val response = try {
            Log.e("DELETESONG", "ENTERING DELETE SONG FUNCTION")

            retrofitInstance.api.deleteFeedback(feedbackID)
        } catch (e: IOException) {
            Log.e("HOMEFRAGMENT-GETGOOD", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("HOMEFRAGMENT-GETGOOD", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            Log.e("deleteFeedback", "Feedback Deleted")
        }

        return true
    }

    fun Refresh(updatedList: List<feedbackResponse>){
        differ.submitList(updatedList)
    }

}




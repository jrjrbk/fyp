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
import com.example.testsem7v1.databinding.ItemAlbumBinding
import com.example.testsem7v1.databinding.ItemPlaylistBinding
import com.example.testsem7v1.databinding.ItemPlaylistRecommendationsBinding
import com.example.testsem7v1.interfaces.onDeleteSongClickListener
import com.example.testsem7v1.interfaces.spotifyItemClickListener
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.spotify.albumTrack.Item
import com.example.testsem7v1.retrofit.spotify.getSeveralTrack.Track
import com.example.testsem7v1.retrofit.systemDatabase.playlistDataItem
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException
import java.lang.IllegalArgumentException

class itemPlaylistAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TrackITEM = 0
    private val RecItem = 2

    lateinit var listener: onDeleteSongClickListener
    inner class ViewHolder(val binding: ItemPlaylistBinding): RecyclerView.ViewHolder(binding.root)
    inner class ViewHolder2(val binding2: ItemPlaylistRecommendationsBinding): RecyclerView.ViewHolder(binding2.root)


    // DiffCallBack 1
    private val diffCallback = object: DiffUtil.ItemCallback<Track>(){
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this,diffCallback)
    var trackItem: List<Track>
        get() = differ.currentList
        set(value) {differ.submitList(value)}

    // DiffCallBack 2
    private val diffCallback2 = object: DiffUtil.ItemCallback<com.example.testsem7v1.retrofit.spotify.recommendation.Track>(){
        override fun areItemsTheSame(
            oldItem: com.example.testsem7v1.retrofit.spotify.recommendation.Track,
            newItem: com.example.testsem7v1.retrofit.spotify.recommendation.Track
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: com.example.testsem7v1.retrofit.spotify.recommendation.Track,
            newItem: com.example.testsem7v1.retrofit.spotify.recommendation.Track
        ): Boolean {
            return oldItem == newItem
        }
    }

    private val differ2 = AsyncListDiffer(this, diffCallback2)
    var recItem: List<com.example.testsem7v1.retrofit.spotify.recommendation.Track>
        get() = differ2.currentList
        set(value) {differ2.submitList(value)}


//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding.apply{
//            val trackItem = trackItem[position]
//            playlistItemSongName.text = trackItem.name
//            playlistNumber.text = "${position+1}"
//            if(trackItem.explicit){
//                playlistItemArtistName.text = "\uD83C\uDD74${trackItem.artists[0].name}"
//            }
//            playlistItemArtistName.text = trackItem.artists[0].name
//
//            itemPlaylistDelete.setOnClickListener (object: View.OnClickListener{
//                override fun onClick(v: View?) {
//                    //delete database file and refresh
//                    val activity: AppCompatActivity = v!!.context as AppCompatActivity
//                    var delete = false
//
//                    Log.e(
//                        "DELETEPLAYLIST",
//                        "Position: ${holder.adapterPosition}"
//                    )
//
//                    val builder = AlertDialog.Builder(activity)
//                    builder.setMessage("Do you want to delete ${trackItem.name} from your playlist?")
//                    builder.setTitle("Delete")
//                    builder.setCancelable(false)
//                    builder.setPositiveButton("Yes"){
//                            dialog, which ->
//                        delete = true
//                        Log.e("DELETE PRESSED","Yes is clicked, isDeleteTrue: ${delete}")
//                        activity.lifecycleScope.launch {
//                            deleteSong(trackItem.id)
//
//                            listener.passData(trackItem.id)
//
//                        }
//                    }
//                    builder.setNegativeButton("No"){
//                            dialog, which ->
//                        dialog.cancel()
//                        Log.e("DELETE PRESSED","No is clicked, isDeleteTrue: ${delete}")
//
//                    }
//
//                    val alertDialog = builder.create()
//                    alertDialog.show()
//
//                }
//
//            })
//        }
//
//    }

    override fun getItemViewType(position: Int): Int {

//        if (recItem.isEmpty()){
//            return TrackITEM
//        }
//
//        //0 or 2
//        // trackItem.size%trackItem.size = 0
//        // trackitem = 15, recitem= 10
//        // total size = 25
//        // pos 1 to 15 is 0, pos 16 to 25 is = 2?
//
//        // position ->
//
//        //track =5 , rec = 5
//        // 10
//        // 0,1,2,3,4

        return when{
            position < trackItem.size -> return TrackITEM
            position >= trackItem.size -> return RecItem
            else -> throw IllegalArgumentException("Error at position $position")
        }

    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is ViewHolder ->{
                holder.binding.apply{
                    val trackItem = trackItem[position]
                    playlistItemSongName.text = trackItem.name
                    playlistNumber.text = "${position+1}"
                    if(trackItem.explicit){
                        playlistItemArtistName.text = "\uD83C\uDD74${trackItem.artists[0].name}"
                    }
                    playlistItemArtistName.text = trackItem.artists[0].name

                    itemPlaylistDelete.setOnClickListener (object: View.OnClickListener{
                        override fun onClick(v: View?) {
                            //delete database file and refresh
                            val activity: AppCompatActivity = v!!.context as AppCompatActivity
                            var delete = false

                            Log.e(
                                "DELETEPLAYLIST",
                                "Position: ${holder.adapterPosition}"
                            )

                            val builder = AlertDialog.Builder(activity)
                            builder.setMessage("Do you want to delete ${trackItem.name} from your playlist?")
                            builder.setTitle("Delete")
                            builder.setCancelable(false)
                            builder.setPositiveButton("Yes"){
                                    dialog, which ->
                                delete = true
                                Log.e("DELETE PRESSED","Yes is clicked, isDeleteTrue: ${delete}")
                                activity.lifecycleScope.launch {
                                    deleteSong(trackItem.id)

                                    listener.passData(trackItem.id)

                                }
                            }
                            builder.setNegativeButton("No"){
                                    dialog, which ->
                                dialog.cancel()
                                Log.e("DELETE PRESSED","No is clicked, isDeleteTrue: ${delete}")

                            }

                            val alertDialog = builder.create()
                            alertDialog.show()

                        }

                    })
                }
            }

            is ViewHolder2 ->{
                // view holder 2 recommendations.
                holder.binding2.apply {
                    val recItem = recItem[position-trackItem.size]
                    playlistItemArtistNameR.text = recItem.artists[0].name
                    val recommendedText = "(RECOMMENDED)"
                    val color = ""
                    val colorChange = "<font color ="
                    playlistItemSongNameR.text = "${recItem.name} "
//                    playlistItemSongNameR.text = Html.
                }
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            TrackITEM ->  ViewHolder(ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            RecItem ->  ViewHolder2(ItemPlaylistRecommendationsBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            else -> throw IllegalArgumentException("Invalid View Type")
        }
    }

    override fun getItemCount(): Int {
        if(recItem.isEmpty()){
            return trackItem.size
        }

        return trackItem.size + recItem.size
    }

    private suspend fun deleteSong(spotifyRef: String): Boolean{

        val response = try {
            Log.e("DELETESONG", "ENTERING DELETE SONG FUNCTION")

            retrofitInstance.api.deleteSong(spotifyRef)
        } catch (e: IOException) {
            Log.e("HOMEFRAGMENT-GETGOOD", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("HOMEFRAGMENT-GETGOOD", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            Log.e("deleteSong", "Song Deleted")
        }

        return true
    }

    fun Refresh(updatedList: List<Track>){
        Log.e("test", "${updatedList}")
        differ.submitList(updatedList)
    }

    fun RefreshRec(updatedList: List<com.example.testsem7v1.retrofit.spotify.recommendation.Track>){
        differ2.submitList(updatedList)
    }
}
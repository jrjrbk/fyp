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
import com.example.testsem7v1.databinding.PlaylistItemBinding
import com.example.testsem7v1.interfaces.OnPlaylistItemClickListener
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.systemDatabase.playlistDataItem
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class mainPlaylistAdapter: RecyclerView.Adapter<mainPlaylistAdapter.ViewHolder>(){

    lateinit var listener: OnPlaylistItemClickListener

//    init {
//        this.listener =
//    }
    class ViewHolder (val binding: PlaylistItemBinding): RecyclerView.ViewHolder(binding.root) {

    }

    private val diffCallBack = object: DiffUtil.ItemCallback<playlistDataItem>(){
        override fun areItemsTheSame(
            oldItem: playlistDataItem,
            newItem: playlistDataItem
        ): Boolean {
            Log.e("DiffCallback1", "Test")
            return oldItem.playlistID == newItem.playlistID
        }

        override fun areContentsTheSame(
            oldItem: playlistDataItem,
            newItem: playlistDataItem
        ): Boolean {
            Log.e("DiffCallback2", "Test")

            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this,diffCallBack)
    var mainPlaylist: List<playlistDataItem>
        get() = differ.currentList
        set(value){differ.submitList(value)}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(PlaylistItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return mainPlaylist.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply{
            playlistImage.clipToOutline = true
            val mainPlaylistItem = mainPlaylist[position]
            playlistName.text =mainPlaylistItem.playlistName
            playlistdateCreated.text = mainPlaylistItem.playlistDate.subSequence(0,10)
            Picasso.get().load(mainPlaylistItem.playlistImage).into(playlistImage)

            deletePlaylist.setOnClickListener (object: View.OnClickListener{
                override fun onClick(v: View?) {
                    //delete database file and refresh
                    val activity: AppCompatActivity = v!!.context as AppCompatActivity
                    var delete = false

                    Log.e(
                        "DELETEPLAYLIST",
                        "playlistName: ${playlistName.text}, Position: ${holder.adapterPosition}, PlaylistID: ${mainPlaylistItem.playlistID}"
                    )

                    val builder = AlertDialog.Builder(activity)
                    builder.setMessage("Do you want to delete playlist ${mainPlaylistItem.playlistName}?")
                    builder.setTitle("Alert!")
                    builder.setCancelable(false)
                    builder.setPositiveButton("Yes"){
                        dialog, which ->
                            delete = true
                            Log.e("DELETE PRESSED","Yes is clicked, isDeleteTrue: ${delete}")
                            activity.lifecycleScope.launch {
                            deletePlaylist(mainPlaylistItem.playlistID)
                            //refresh the list of current RecyclerView

                                //Refreshing the RV
                                var listTemp = mainPlaylist.toMutableList()
                                Log.e("test", "${listTemp}")
                                listTemp.removeAt(holder.adapterPosition)
                                Log.e("test", "${listTemp}")
                                differ.submitList(listTemp)

                            notifyItemRemoved(holder.adapterPosition)
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


            playlistItem.setOnClickListener (object: View.OnClickListener{
                override fun onClick(v: View?) {
                    val activity: AppCompatActivity = v!!.context as AppCompatActivity
                    Log.e(
                        "PlaylistItemClicked",
                        "playlistName: ${playlistName.text}, Position: ${holder.adapterPosition}, PlaylistID: ${mainPlaylistItem.playlistID}"
                    )

                    listener.passData(mainPlaylistItem.playlistID)
                }

            })
        }
    }


    private suspend fun deletePlaylist(id: Int): Boolean{

        val response = try {
            retrofitInstance.api.deletePlaylist(id)
        } catch (e: IOException) {
            Log.e("HOMEFRAGMENT-GETGOOD", "IOException, you might not have internet connection")
            return false

        } catch (e: HttpException) {
            Log.e("HOMEFRAGMENT-GETGOOD", "HttpException, unexpected response")
            return false
        }
        if (response.isSuccessful && response.body() != null) {
            Log.e("deletePlaylist", "Playlist Deleted")
        }

        return true
    }

    fun Refresh(updatedList: List<playlistDataItem>){
        Log.e("test", "${updatedList}")
        differ.submitList(updatedList)
    }


}




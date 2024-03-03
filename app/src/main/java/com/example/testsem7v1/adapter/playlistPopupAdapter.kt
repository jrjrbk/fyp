package com.example.testsem7v1.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.databinding.ItemAddPlaylistBinding
import com.example.testsem7v1.databinding.PlaylistItemBinding
import com.example.testsem7v1.interfaces.OnPlaylistItemClickListener
import com.example.testsem7v1.retrofit.retrofitInstance
import com.example.testsem7v1.retrofit.systemDatabase.playlistDataItem
import com.example.testsem7v1.retrofit.systemDatabase.playlistMetadata
import com.squareup.picasso.Picasso
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.IOException
import retrofit2.HttpException

class playlistPopupAdapter: RecyclerView.Adapter<playlistPopupAdapter.ViewHolder>(){

    lateinit var listener: OnPlaylistItemClickListener
    lateinit var metaData: List<playlistMetadata>

    class ViewHolder (val binding: ItemAddPlaylistBinding): RecyclerView.ViewHolder(binding.root) {

    }

    private val diffCallBack = object: DiffUtil.ItemCallback<playlistDataItem>(){
        override fun areItemsTheSame(
            oldItem: playlistDataItem,
            newItem: playlistDataItem
        ): Boolean {
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
        return ViewHolder(ItemAddPlaylistBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return mainPlaylist.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply{
            addToPlaylistImage.clipToOutline = true
            val mainPlaylistItem = mainPlaylist[position]
            addPlaylistName.text =mainPlaylistItem.playlistName
            Picasso.get().load(mainPlaylistItem.playlistImage).into(addToPlaylistImage)

            CoroutineScope(Dispatchers.Main).launch{
                val totalsongs = getPlaylistMeta(mainPlaylistItem.playlistID).toString()
                totalSongAddPlaylist.text = "$totalsongs songs"
            }

            addToPlaylistItemAdd.setOnClickListener{
                listener.passData(mainPlaylistItem.playlistID)
            }

        }
    }


    private suspend fun getPlaylistMeta(id: Int): Int{

        val response = try {
            retrofitInstance.api.getPlaylistMetadata(id)
        } catch (e: IOException) {
            Log.e("HOMEFRAGMENT-GETGOOD", "IOException, you might not have internet connection")
            return -1

        } catch (e: HttpException) {
            Log.e("HOMEFRAGMENT-GETGOOD", "HttpException, unexpected response")
            return -1
        }
        if (response.isSuccessful && response.body() != null) {
            metaData = response.body()!!
        }

        return metaData.size
    }

    fun Refresh(updatedList: List<playlistDataItem>){
        Log.e("test", "${updatedList}")
        differ.submitList(updatedList)
    }


}



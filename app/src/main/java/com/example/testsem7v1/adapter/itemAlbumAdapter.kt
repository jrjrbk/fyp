package com.example.testsem7v1.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.databinding.ItemAlbumBinding
import com.example.testsem7v1.databinding.ItemRecommendationBinding
import com.example.testsem7v1.interfaces.spotifyItemClickListener
import com.example.testsem7v1.retrofit.spotify.albumTrack.Item
import com.example.testsem7v1.retrofit.spotify.getTopTrack.Track
import com.squareup.picasso.Picasso

class itemAlbumAdapter: RecyclerView.Adapter<itemAlbumAdapter.ViewHolder>() {

    lateinit var listener: spotifyItemClickListener
    inner class ViewHolder(val binding: ItemAlbumBinding): RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object: DiffUtil.ItemCallback<Item>(){
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this,diffCallback)
    var itemAlbum: List<Item>
        get() = differ.currentList
        set(value) {differ.submitList(value)}


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply{
            val itemAlbum = itemAlbum[position]
            albumItemSongName.text = itemAlbum.name
            trackNumber.text = "${itemAlbum.track_number}."
            if(itemAlbum.explicit){
                albumItemArtistName.text = "\uD83C\uDD74${itemAlbum.artists[0].name}"
            }
            albumItemArtistName.text = itemAlbum.artists[0].name
            Log.e("ITEMALBUMDAPATER", itemAlbum.name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return itemAlbum.size
    }
}
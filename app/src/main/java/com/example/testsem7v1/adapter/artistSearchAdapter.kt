package com.example.testsem7v1.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.databinding.ItemArtistsearchBinding
import com.example.testsem7v1.interfaces.spotifyItemClickListener
import com.example.testsem7v1.retrofit.spotify.artist.Item
import com.squareup.picasso.Picasso


class artistSearchAdapter: RecyclerView.Adapter<artistSearchAdapter.ViewHolder>(){
    //viewholder item

    lateinit var listener: spotifyItemClickListener
    class ViewHolder (val binding: ItemArtistsearchBinding): RecyclerView.ViewHolder(binding.root){
    }

    //DiffUtil
    private val diffCallBack = object: DiffUtil.ItemCallback<Item>(){
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.href == newItem.href
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this,diffCallBack)
    var artist: List<Item>
        get() = differ.currentList
        set(value){differ.submitList(value)}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemArtistsearchBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return artist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply{
            artistSearchImage.clipToOutline = true
            Log.e("ArtistAdapter", "Position: $position")
            val artists = artist[position]
            artistSearchName.text = artists.name

            if(artists.images.isNotEmpty()){
                Log.e("ArtistAdapter", artists.images[1].url)
                Log.e("ArtistAdapter", "Position: $position not empty")
                Picasso.get().load(artists.images[1].url).into(artistSearchImage)}

            artistSearchItem.setOnClickListener {
                listener.passArtistRef(artists.id)
            }
        }
    }
}
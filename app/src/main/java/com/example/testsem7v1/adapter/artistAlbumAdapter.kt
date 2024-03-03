package com.example.testsem7v1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.databinding.ItemRecommendationBinding
import com.example.testsem7v1.interfaces.spotifyItemClickListener
import com.example.testsem7v1.retrofit.spotify.getArtistAlbum.Item
import com.squareup.picasso.Picasso

class artistAlbumAdapter: RecyclerView.Adapter<artistAlbumAdapter.ViewHolder>() {

    lateinit var listener: spotifyItemClickListener
    inner class ViewHolder(val binding: ItemRecommendationBinding): RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object: DiffUtil.ItemCallback<Item>(){
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this,diffCallback)
    var artistAlbum: List<Item>
        get() = differ.currentList
        set(value) {differ.submitList(value)}

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply{
            imgRecommendation.clipToOutline = true
            val artistAlbum = artistAlbum[position]
            recommendationTitleTV.text = artistAlbum.name
            artistNameHomeTV.text = artistAlbum.release_date.subSequence(0,4)
            Picasso.get().load(artistAlbum.images[1].url).into(imgRecommendation)

            homRecommendationItem.setOnClickListener {
                listener.passAlbumRef(artistAlbum.id)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return artistAlbum.size
    }
}
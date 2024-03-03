package com.example.testsem7v1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.databinding.ItemRecommendationBinding
import com.example.testsem7v1.databinding.ItemRelatedArtistBinding
import com.example.testsem7v1.interfaces.spotifyItemClickListener
import com.example.testsem7v1.retrofit.spotify.relatedArtist.Artist
import com.squareup.picasso.Picasso

class artistRelatedAdapter: RecyclerView.Adapter<artistRelatedAdapter.ViewHolder>() {

    lateinit var listener: spotifyItemClickListener
    inner class ViewHolder(val binding: ItemRelatedArtistBinding): RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object: DiffUtil.ItemCallback<Artist>(){
        override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this,diffCallback)
    var relatedArtist: List<Artist>
        get() = differ.currentList
        set(value) {differ.submitList(value)}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRelatedArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply{
            imgRelatedArtist.clipToOutline = true
            val relatedArtist = relatedArtist[position]
            relatedArtistNameTV.text = relatedArtist.name
            relatedArtistFollower.text = relatedArtist.followers.total.toString()
            Picasso.get().load(relatedArtist.images[1].url).into(imgRelatedArtist)

            relatedArtistItem.setOnClickListener {
                listener.passArtistRef(relatedArtist.id)
            }
        }    }


    override fun getItemCount(): Int {
        return relatedArtist.size
    }
}
package com.example.testsem7v1.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.databinding.ItemRecommendationBinding
import com.example.testsem7v1.interfaces.spotifyItemClickListener
import com.example.testsem7v1.retrofit.spotify.getTopTrack.Track
import com.squareup.picasso.Picasso

class topTrackAdapter: RecyclerView.Adapter<topTrackAdapter.ViewHolder>() {

    lateinit var listener: spotifyItemClickListener
    inner class ViewHolder(val binding: ItemRecommendationBinding): RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object: DiffUtil.ItemCallback<Track>(){
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this,diffCallback)
    var topTrack: List<Track>
        get() = differ.currentList
        set(value) {differ.submitList(value)}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): topTrackAdapter.ViewHolder {
        return ViewHolder(ItemRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: topTrackAdapter.ViewHolder, position: Int) {
        holder.binding.apply{
            imgRecommendation.clipToOutline = true
            val topTrack = topTrack[position]
            recommendationTitleTV.text = topTrack.name
            artistNameHomeTV.text = topTrack.artists[0].name
            Picasso.get().load(topTrack.album.images[1].url).into(imgRecommendation)

            homRecommendationItem.setOnClickListener {
                listener.passTrackRef(topTrack.id)
            }

            Log.e("TOPTRACKADAPTER", topTrack.name)

        }
    }

    override fun getItemCount(): Int {
        return topTrack.size
    }
}
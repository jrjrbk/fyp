package com.example.testsem7v1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.databinding.ItemRecommendationBinding
import com.example.testsem7v1.retrofit.spotify.recommendation.Track
import com.squareup.picasso.Picasso

class homeMusicRVAdapter: RecyclerView.Adapter<homeMusicRVAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemRecommendationBinding): RecyclerView.ViewHolder(binding.root) {
    }




    private val diffCallback = object: DiffUtil.ItemCallback<Track>(){
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem == newItem
        }
    }

    // Actual list differ. Which updates list.
    private val differ = AsyncListDiffer(this, diffCallback)
    var recommendationTrack: List<Track>
        get()= differ.currentList
        set(value) {differ.submitList(value)}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return recommendationTrack.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply{
            imgRecommendation.clipToOutline = true
            val recommendations = recommendationTrack[position]
            artistNameHomeTV.text = recommendations.artists[0].name
            recommendationTitleTV.text = recommendations.name
            Picasso.get().load(recommendations.album.images[1].url).into(imgRecommendation)
        }
    }
}
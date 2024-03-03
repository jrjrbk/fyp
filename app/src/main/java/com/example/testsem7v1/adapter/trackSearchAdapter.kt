package com.example.testsem7v1.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.databinding.ItemAlbumsearchBinding
import com.example.testsem7v1.interfaces.spotifyItemClickListener
import com.example.testsem7v1.retrofit.spotify.track.Item
import com.squareup.picasso.Picasso

class trackSearchAdapter: RecyclerView.Adapter<trackSearchAdapter.ViewHolder>() {
    // Try guna album search item lah kan eh.

    lateinit var listener: spotifyItemClickListener
    class ViewHolder (val binding: ItemAlbumsearchBinding): RecyclerView.ViewHolder(binding.root) {
    }

    private val diffCallBack = object: DiffUtil.ItemCallback<Item>(){
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.href == newItem.href
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this,diffCallBack)
    var tracks: List<Item>
        get() = differ.currentList
        set(value){differ.submitList(value)}

    //change view holder here later
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAlbumsearchBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply{
            val tracks = tracks[position]
            albumTitle.text = tracks.name
            artistTitle.text = tracks.artists[0].name
            val lengthText = ((tracks.duration_ms/1000.00)/60.00)
            val lengthTextSeconds = ((tracks.duration_ms/1000.00)%60.00)
            albumSearchYear.text = "${lengthText.toInt()}:${lengthTextSeconds.toInt()}"
            Picasso.get().load(tracks.album.images[1].url).into(albumImage)

            albumSearchItem.setOnClickListener {
                listener.passTrackRef(tracks.id)
            }
        }
    }
}
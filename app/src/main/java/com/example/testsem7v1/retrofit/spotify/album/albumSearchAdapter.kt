package com.example.testsem7v1.retrofit.spotify.album

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.artistData
import com.example.testsem7v1.databinding.ItemAlbumsearchBinding
import com.squareup.picasso.Picasso

class albumSearchAdapter: RecyclerView.Adapter<albumSearchAdapter.ViewHolder>(){

    // Binds this adapter to recycler item
    class ViewHolder (val binding: ItemAlbumsearchBinding): RecyclerView.ViewHolder(binding.root){

    }

    // Diffutil thank god for this
    // Type of item album.item(Data)
    private val diffCallBack = object: DiffUtil.ItemCallback<Item>(){
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.href ==newItem.href
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    // Get the album response. Then
    private val differ = AsyncListDiffer(this,diffCallBack)
    var albums: List<Item>
        get() = differ.currentList
        set(value){differ.submitList(value)}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAlbumsearchBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun getItemCount(): Int {
        return albums.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            Log.e("SearchADAPTER", albums.size.toString())
            val albums = albums[position]
            albumTitle.text = albums.name
            artistTitle.text = albums.artists[0].name
            Picasso.get().load(albums.images[2].url).into(albumImage)
        }
    }
}
package com.example.testsem7v1.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.databinding.HomervItemBinding
import com.example.testsem7v1.retrofit.spotify.test

class homervAdapter: RecyclerView.Adapter<homervAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: HomervItemBinding): RecyclerView.ViewHolder(binding.root) {
    }

    private val diffCallback = object: DiffUtil.ItemCallback<test>(){
        override fun areItemsTheSame(oldItem: test, newItem: test): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: test, newItem: test): Boolean {
            return oldItem == newItem
        }

    }

    // Actual list differ. Which updates list.

    private val differ = AsyncListDiffer(this, diffCallback)
    var artist: List<test>
        get()= differ.currentList
        set(value) {differ.submitList(value)}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(HomervItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return artist.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply{
            val artist = artist[position]
            tvHomeRV.text =artist.name
        }
    }
}
package com.example.testsem7v1.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.testsem7v1.ACRCloud.humming.Humming
import com.example.testsem7v1.ACRCloud.humming.acrHumResponse
import com.example.testsem7v1.databinding.ItemIdentifiedHumBinding

class identifiedHumAdapter: RecyclerView.Adapter<identifiedHumAdapter.ViewHolder> () {
    class ViewHolder(val binding: ItemIdentifiedHumBinding): RecyclerView.ViewHolder(binding.root)

    private val diffCallBack = object: DiffUtil.ItemCallback<Humming>(){


        override fun areItemsTheSame(oldItem: Humming, newItem: Humming): Boolean {
            return oldItem.acrid == newItem.acrid
        }

        override fun areContentsTheSame(oldItem: Humming, newItem: Humming): Boolean {
            return oldItem == newItem
        }
    }

    private val differ =AsyncListDiffer(this,diffCallBack)
    var acrData: List<Humming>
        get() = differ.currentList
        set(value){differ.submitList(value)}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemIdentifiedHumBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return acrData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            val acrData = acrData[position]
            identifiedHumTitle.text = acrData.title
            identifiedHumArtist.text = acrData.artists[0].name
            identifiedHumScore.text = "Score ${acrData.score}"
        }
    }
}
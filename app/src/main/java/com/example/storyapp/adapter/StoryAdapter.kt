package com.example.storyapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.StoryItemBinding
import com.example.storyapp.model.StoryModel

class StoryAdapter(private val listStory: ArrayList<StoryModel>) :
    RecyclerView.Adapter<StoryAdapter.ListViewHolder>() {
    private lateinit var binding: StoryItemBinding
    private lateinit var onItemClickCallback: OnItemClickCallback
    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        binding = StoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {

        val stories = listStory[position]
        with(binding) {
            textViewStory.text = stories.name
            Glide.with(binding.root.context)
                .load(stories.photoUrl)
                .circleCrop()
                .into(avatar)
            root.setOnClickListener {
                onItemClickCallback.onItemClicked(stories)
            }
        }
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listStory[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listStory.size

    class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnItemClickCallback {
        fun onItemClicked(data: StoryModel)
    }
}
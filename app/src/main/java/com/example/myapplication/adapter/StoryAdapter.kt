package com.example.myapplication.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myapplication.activity.DetailStoryActivity
import com.example.myapplication.response.ListStoryItem
import com.example.myapplication.databinding.ItemStoryBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class StoryAdapter : PagingDataAdapter<ListStoryItem,StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {
    inner class StoryViewHolder (private val binding : ItemStoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.ivItemName.text = story.name
            Glide.with(binding.root.context).load(story.photoUrl).into(binding.ivItemPhoto)

            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val date: Date = inputFormat.parse(story.createdAt) as Date
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

            val formattedDate: String = outputFormat.format(date)
            val formattedTime:String = timeFormat.format(date)
            binding.ivItemDate.text = formattedDate
            binding.ivItemTime.text = formattedTime

            binding.root.setOnClickListener {
                // Navigate to detail story activity
                val context = binding.root.context
                val intent = Intent(context, DetailStoryActivity::class.java).apply {
                    putExtra("name", story.name)
                    putExtra("description", story.description)
                    putExtra("imageUrl", story.photoUrl)
                }
                context.startActivity(intent)
            }


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(binding)
    }


    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val story = getItem(position)
        Log.d("StoryAdapter", "Binding story at position $position: $story")
        if (story != null) {
            holder.bind(story)
        }
    }

    companion object {
         val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }


}